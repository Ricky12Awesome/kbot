package me.ricky.discord.bot.kbot.command.other

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class XPCommand : Command {
  override val name: String = "xp"
  override val description: String = "Gets and sets xp"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(exact(0, 1), optional("xp"))

  override fun CommandEvent.onEvent() {
    when (runAt) {
      0 -> channel.send("Xp: ${member.data.xp}, Level: ${xp.xpToLevel(member.data.xp)}")
      1 -> {
        xp.setXP(member, args[1].toDoubleOrNull() ?: throw exception("Invalid Number"))
        channel.send("xp is now ${args[1]}")
      }
    }
  }
}