package me.ricky.discord.bot.kbot.command.information

import me.ricky.discord.bot.kbot.TODO_PAGE
import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandCategory
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class TODOCommand : Command {
  override val name: String = "todo"
  override val description: String = "links you to $TODO_PAGE"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.SEND_MESSAGES
  override val category: CommandCategory = CommandCategory.INFORMATION
  override val usage: Usage = usage()

  override fun CommandEvent.onEvent() {
    channel.send("Here's the link to the todo page. <$TODO_PAGE>")
  }
}