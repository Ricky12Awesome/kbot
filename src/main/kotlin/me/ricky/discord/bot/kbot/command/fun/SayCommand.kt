package me.ricky.discord.bot.kbot.command.`fun`

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandCategory
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class SayCommand : Command {
  override val name: String = "say"
  override val description: String = "tell the bot to say something"
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val category: CommandCategory = CommandCategory.FUN
  override val usage: Usage = usage(
    runAt(exactOrAfter(1)),
    required("message...")
  )

  override fun CommandEvent.onEvent() {
    message.delete()
    channel.send(args.slice(1..args.lastIndex).joinToString(" "))
  }

}