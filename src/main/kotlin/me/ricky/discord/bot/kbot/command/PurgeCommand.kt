package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.permission.PermissionType

class PurgeCommand : Command {
  override val name: String = "purge"
  override val permission: PermissionType = PermissionType.MANAGE_MESSAGES
  override val description: String = "Purge Command"
  override val usage: Usage = usage(
    runAt(exact(1, 2)),
    required("limit", "from-message-id"),
    optional("to-message-id")
  )

  override fun CommandEvent.onEvent() {
    when (runAt) {
      1 -> purgeAmount()
      2 -> purgeFromTo()
    }
  }

  fun CommandEvent.purgeAmount() {
    val limit = args[1].toIntOrNull()
    val from = args[1].toLongOrNull()

    if (limit != null) {
      if (0 > limit) throw exception("Limit cannot be negative.")
      channel.getMessages(limit).thenAccept { it.deleteAll() }
      channel.sendMessage("Deleted `$limit` messages. :ok_hand:")
    } else if (from != null) {
      channel.getMessagesBetween(from, messageId).thenAccept { it.deleteAll() }
      channel.sendMessage("Deleted all messages from `$from` and downward. :ok_hand:")
    }

  }

  fun CommandEvent.purgeFromTo() {
    val from = args[1].toLongOrNull() ?: throw TODO("From is null. MAKE A COMMAND EXCEPTION FOR THIS")
    val to = args[2].toLongOrNull() ?: throw TODO("To is null. MAKE A COMMAND EXCEPTION FOR THIS")

    channel.getMessagesBetween(from, to).thenAccept { it.deleteAll() }
    channel.sendMessage("Deleted Message from `$from` to `$to` :ok_hand:")
  }

}