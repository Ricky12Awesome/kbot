package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.CHANGE_LOG
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class ChangeLogCommand : Command {
  override val name: String = "changelog"
  override val description: String = "Links you to $CHANGE_LOG"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage()

  override fun CommandEvent.onEvent() {
    channel.send("Here's the link to the change log page. $CHANGE_LOG")
  }
}