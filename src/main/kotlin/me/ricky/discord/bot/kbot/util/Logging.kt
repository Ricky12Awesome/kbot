package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.ServerTextChannel

enum class LogType(val text: String, val color: Color) {
  USED_BANNED("Banned", Color.RED),
  USER_KICKED("Kicked", Color.GOLD),
  USER_MUTED("Muted", Color.DARKGOLDENROD),
  USER_JOINED("Joined", Color.CYAN),
  USER_LEFT("Left", Color.DARKCYAN),
  ROLE_ADDED("Role Added", Color.MEDIUMPURPLE),
  ROLE_REMOVED("Role Remove", Color.PURPLE),
  MESSAGE("Sent", Color.ORANGE),
  MESSAGE_EDITED("Edited", Color.ORANGE),
  MESSAGE_CLEARED("Cleared", Color.ORANGE)
}

interface LogMessage {
  val type: LogType

  fun sendTo(channel: ServerTextChannel)
}

interface PunishmentMessage : LogMessage {
  val fromId: Long
  val toId: Long
  val time: Long
  val reason: String

  override fun sendTo(channel: ServerTextChannel) {
    val from = channel.server.getMemberById(fromId).value?.discriminatedName ?: "Not Found"
    val to = channel.server.getMemberById(toId).value?.discriminatedName ?: "Not Found"

    val embed = embed(
      title = type.text,
      color = type.color,
      description = stringList(
        "**From:** $from ($fromId)",
        "**To:** $to ($toId)",
        "**For:** ${formattedTime(time)}",
        "**Reason:**: $reason"
      )
    )
    channel.send(embed)
  }
}

data class BannedLog(
  override val type: LogType,
  override val fromId: Long,
  override val toId: Long,
  override val time: Long,
  override val reason: String
) : PunishmentMessage