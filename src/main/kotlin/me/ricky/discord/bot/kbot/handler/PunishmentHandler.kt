package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.util.database.MemberTable
import me.ricky.discord.bot.kbot.util.database.PunishmentReport
import me.ricky.discord.bot.kbot.util.database.PunishmentReportTable
import me.ricky.discord.bot.kbot.util.database.PunishmentReportType
import me.ricky.discord.bot.kbot.util.database.SQLMember
import me.ricky.discord.bot.kbot.util.database.SQLServer
import me.ricky.discord.bot.kbot.util.getSQLMember
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.database.sql
import me.ricky.discord.bot.kbot.util.database.sqlSelect
import me.ricky.discord.bot.kbot.util.database.sqlSelectFirst
import me.ricky.discord.bot.kbot.util.database.sqlUpdate
import me.ricky.discord.bot.kbot.util.toSQL
import org.javacord.api.DiscordApi
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateStatement
import kotlin.concurrent.timer

data class PunishmentData(
  val type: PunishmentReportType,
  val reportId: Long,
  val server: SQLServer,
  val member: SQLMember,
  val start: Long,
  val time: Long
)

class PunishmentHandler(val api: DiscordApi) {
  val timers = mutableListOf<PunishmentData>()

  init {
    sqlSelect<PunishmentReportTable>({ it.isCompleted eq false }) { it.forEach { row -> addTimer(row) } }

    timer(period = 500) {
      timers.removeAll { data ->
        val (type, id, server, member, start, time) = data
        val delta = System.currentTimeMillis() - start
        if (delta >= time) {
          sqlUpdate<PunishmentReportTable>(where = { it.reportId eq id }) {
            it[isCompleted] = true
          }
          val msg = when (type) {
            PunishmentReportType.BANNED -> {
              server.unbanUser(member)
              "unbanned"
            }
            PunishmentReportType.MUTED -> {
              member.removeRole(server.muteRole)
              "unmuted"
            }
            else -> ""
          }
          server.logChannel!!.send("${member.discriminatedName} has been $msg")
          return@removeAll true
        }
        return@removeAll false
      }
    }
  }

  fun PunishmentReportTable.addTimer(row: ResultRow) {
    val sqlServer = api.getServerById(row[serverId]).get().toSQL()
    val sqlMember = sqlServer.getSQLMember(row[toId])!!
    val data = PunishmentData(
      type = row[type],
      reportId = row[reportId],
      server = sqlServer,
      member = sqlMember,
      start = row[startTime],
      time = row[time]
    )
    timers.add(data)
  }

  fun mute(
    from: SQLMember,
    toId: SQLMember,
    time: Long,
    reason: String
  ) = punish(PunishmentReportType.MUTED, from, toId, time, reason) { it[mutes] = toId.data.mutes + 1 }

  fun kick(
    from: SQLMember,
    toId: SQLMember,
    reason: String
  ) = punish(PunishmentReportType.KICKED, from, toId, -1, reason) { it[kicks] = toId.data.kicks + 1 }

  fun tempban(
    from: SQLMember,
    toId: SQLMember,
    time: Long,
    reason: String
  ) = punish(PunishmentReportType.BANNED, from, toId, time, reason) { it[bans] = toId.data.bans + 1 }

  fun permban(
    from: SQLMember,
    toId: SQLMember,
    reason: String
  ) = punish(PunishmentReportType.BANNED, from, toId, -1, reason)

  fun punish(
    type: PunishmentReportType,
    from: SQLMember,
    to: SQLMember,
    time: Long,
    reason: String,
    update: (MemberTable.(UpdateStatement) -> Unit)? = null
  ) {
    val report = PunishmentReport(
      type = type,
      serverId = from.serverId,
      fromId = from.id,
      toId = to.id,
      reason = reason,
      time = time
    )

    if (!to.server.hasPermission(to, PermissionType.ADMINISTRATOR)) when (type) {
      PunishmentReportType.BANNED -> from.server.banUser(to)
      PunishmentReportType.KICKED -> from.server.kickUser(to)
      PunishmentReportType.MUTED -> to.addRole(from.sqlServer.muteRole)
    }

    sql { PunishmentReportTable.insert(report) }
    sqlSelectFirst<PunishmentReportTable>(
      where = { (it.serverId eq from.serverId) and (it.toId eq to.id) and (it.isCompleted eq false) }
    ) {
      addTimer(it)
    }

    if (update != null) to.sqlUpdate { update(it) }

    report.sendTo(from.sqlServer.logChannel ?: throw exception("Invalid Log Channel"))
  }

}