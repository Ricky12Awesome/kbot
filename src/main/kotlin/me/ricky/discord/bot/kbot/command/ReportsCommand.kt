package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.ReportTable
import me.ricky.discord.bot.kbot.util.SQLMember
import me.ricky.discord.bot.kbot.util.getSQLMember
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.sqlSelect
import org.javacord.api.entity.permission.PermissionType

class ReportsCommand : Command {
  override val name: String = "reports"
  override val description: String = "Gives list of reports for a member"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    exact(0, 1, 2),
    optional("member", "page"),
    optional("page")

  )

  override fun CommandEvent.onEvent() {
    when (runAt) {
      0 -> reportsOfUser(member)
      1 -> oneArg()
      2 -> twoArg()
    }
  }

  fun CommandEvent.oneArg() {
    val page = args[1].toIntOrNull()
    if (page != null) {
      reportsOfUser(member, page)
      return
    }

    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull()
    if (id != null) {
      val user = server.getSQLMember(id) ?: throw exception("User doesn't exist")
      reportsOfUser(user)
      return
    }

    throw exception("Invalid Page or User")
  }

  fun CommandEvent.twoArg() {
    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull() ?: throw exception("Invalid User")
    val page = args[2].toIntOrNull() ?: throw exception("Invalid Page")
    val user = server.getSQLMember(id) ?: throw exception("User doesn't exist")

    reportsOfUser(user, page)
  }

  private fun CommandEvent.reportsOfUser(member: SQLMember, page: Int = 0) = buildString {
    sqlSelect<ReportTable>(where = { it.reportedUserId eq member.id }) { query ->
      query.orderBy(reportId).limit(5, page * 5).forEachIndexed { index, row ->
        appendln("**(${(index + 1) + (page * 5)}):** ${row[reason]}")
      }
    }

    channel.send(
      title = "Reports",
      author = member,
      description = toString()
    )

  }
}
