package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.permission.PermissionType

class PurgeCommand : Command {
  override val name = "purge"
  override val permission = PermissionType.MANAGE_MESSAGES
  override val description = "Purge Command"
  override val usage = usage(
    runAt(exact(2, 4)),
    required("limit", "user"),
    optional(
      required("from-message-id"),
      required("to-message-id")
    )
  )

  override fun CommandEvent.onEvent() {
    val limit = args[1].toIntOrNull() ?: TODO("Make Exception for this, NOW!")


  }

}
