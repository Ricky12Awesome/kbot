package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.permission.PermissionType

class SayCommand : Command {
  override val name: String = "say"
  override val description: String = "tell the bot to say something"
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    runAt(exactOrAfter(1)),
    required("message...")
  )

  override fun CommandEvent.onEvent() {
    message.delete()
    channel.sendMessage(args.slice(1..args.lastIndex).joinToString(" "))
  }

}