package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.avatarUrl
import me.ricky.discord.bot.kbot.util.reports
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.user.User

class ReportsCommand : Command {
  override val name: String = "reports"
  override val description: String = "Gives list of reports for a user"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    exact(0, 1, 2),
    optional("user", "page"),
    optional("page")

  )

  override fun CommandEvent.onEvent() {
    when (runAt) {
      0 -> reportsOfUser(user)
      1 -> oneArg()
      2 -> twoArg()
    }
  }

  fun CommandEvent.oneArg() {
    val page = args[1].toIntOrNull()
    if (page != null) {
      reportsOfUser(user, page)
      return
    }

    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull()
    if (id != null) {
      val user = server.getMemberById(id).value ?: throw exception("User doesn't exist")
      reportsOfUser(user)
      return
    }

    throw exception("Invalid Page or User")
  }

  fun CommandEvent.twoArg() {
    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull() ?: throw exception("Invalid User")
    val page = args[2].toIntOrNull() ?: throw exception("Invalid Page")
    val user = server.getMemberById(id).value ?: throw exception("User doesn't exist")

    reportsOfUser(user, page)
  }

  fun CommandEvent.reportsOfUser(user: User, page: Int = 0) {
    buildString {
      user.reports(serverId, 5, page * 5) {
        appendln("**(${it[count]}):** ${it[reason]}")
      }.thenAcceptAsync {
        channel.send(
          title = "Reports",
          thumbnailUrl = user.avatarUrl,
          description = toString()
        )
      }
    }
  }
}
