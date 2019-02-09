package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.MemberTable
import me.ricky.discord.bot.kbot.util.Report
import me.ricky.discord.bot.kbot.util.ReportTable
import me.ricky.discord.bot.kbot.util.getSQLMember
import me.ricky.discord.bot.kbot.util.sql
import me.ricky.discord.bot.kbot.util.sqlSelectFirst
import me.ricky.discord.bot.kbot.util.sqlUpdate
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.update

class ReportCommand : Command {
  override val name: String = "report"
  override val description: String = "Reports a member."
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    exactOrAfter(2),
    required("report-member"),
    required("reason...")
  )

  override fun CommandEvent.onEvent() {
    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull()
      ?: throw exception("Invalid User")
    val member = server.getSQLMember(id) ?: throw exception("User doesn't exist.")
    val reason = args.slice(2..args.lastIndex).joinToString(" ")
    val current = member.sqlSelectFirst { it[reports] }
    val report = Report(server.id, id, reason)
    member.sqlUpdate { it[reports] = current + 1 }
    sql { ReportTable.insert(report) }
    report.sendTo(channel)
  }


}
