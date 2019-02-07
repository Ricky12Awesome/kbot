package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.getMember
import me.ricky.discord.bot.kbot.util.getSQLMember
import me.ricky.discord.bot.kbot.util.info
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class UserInfoCommand : Command {
  override val name: String = "userinfo"
  override val description: String = "Gives information about yourself or a given member"
  override val aliases: List<String> = listOf("ui")
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    exact(0, 1),
    optional("member")
  )

  override fun CommandEvent.onEvent() {
    when(runAt) {
      0 -> member.info().thenAcceptAsync { channel.send(it) }
      1 -> {
        val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull()
          ?: throw exception("Invalid User")
        val user = server.getSQLMember(id) ?: throw exception("User doesn't exist.")
        user.info().thenAcceptAsync { channel.send(it) }
      }
    }
  }

}