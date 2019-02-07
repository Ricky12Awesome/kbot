package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.Report
import me.ricky.discord.bot.kbot.util.ReportTable
import me.ricky.discord.bot.kbot.util.UserTable
import me.ricky.discord.bot.kbot.util.sql
import me.ricky.discord.bot.kbot.util.throwException
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.update

class ReportCommand : Command {
  override val name: String = "report"
  override val description: String = "Reports a user."
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    exactOrAfter(2),
    required("report-user"),
    required("reason...")
  )

  override fun CommandEvent.onEvent() {
    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull()
      ?: throw exception("Invalid User")
    val user = server.getMemberById(id).value ?: throw exception("User doesn't exist.")
    val reason = args.slice(2..args.lastIndex).joinToString(" ")

    user.sql(serverId) { row ->
      val current = row[reports]
      val report = Report(server.id, current, id, reason)
      UserTable.update { it[reports] = current + 1 }
      ReportTable.insert(report)
      report.sendTo(channel)
    }

  }
}
