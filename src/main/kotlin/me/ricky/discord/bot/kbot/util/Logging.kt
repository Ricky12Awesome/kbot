package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.ServerTextChannel

enum class RoleReportType(override val text: String, override val color: Color) : ReportType {
  ROLE_ADDED("Role Added", Color.MEDIUMPURPLE),
  ROLE_REMOVED("Role Removed", Color.PURPLE),
}

enum class UserReportType(override val text: String, override val color: Color) : ReportType {
  USER_JOINED("Joined", Color.CYAN),
  USER_LEFT("Left", Color.DARKCYAN)
}

enum class MessageReportType(override val text: String, override val color: Color) : ReportType {
  MESSAGE_SENT("Sent", Color.GRAY),
  MESSAGE_EDITED("Edited", Color.DARKGRAY),
  MESSAGE_CLEARED("Cleared", Color.WHITE)
}

enum class PunishmentReportType(override val text: String, override val color: Color) : ReportType {
  USER_BANNED("Banned", Color.RED),
  USER_KICKED("Kicked", Color.ORANGE),
  USER_MUTED("Muted", Color.YELLOW)
}

enum class PunishmentCompletedReportType(override val text: String, override val color: Color) : ReportType {
  USER_UN_BANNED("Unbanned", Color.LIME),
  USER_UN_MUTED("Unmuted", Color.GREEN)
}

interface ReportType {
  val text: String
  val color: Color
}

interface ReportMessage {
  val type: ReportType

  fun sendTo(channel: ServerTextChannel)
}

interface ReportReportMessage : ReportMessage {
  val reportedUserId: Long
  val reason: String
  override val type: ReportType get() = object : ReportType {
    override val text: String = "Report"
    override val color: Color = Color.LIGHTBLUE
  }

  override fun sendTo(channel: ServerTextChannel) {
    val user = channel.server.getRoleById(reportedUserId).value?.name ?: "Not Found"

    channel.send(
      title = type.text,
      color = type.color,
      description = "$user ($reportedUserId) was reported for `$reason`"
    )
  }
}

interface RoleMessage : ReportMessage {
  override val type: RoleReportType
  val roleId: Long
  val userId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val role = channel.server.getRoleById(roleId).value?.name ?: "Not Found"
    val user = channel.server.getMemberById(userId).value?.name ?: "Not Found"
    val msg = when (type) {
      RoleReportType.ROLE_ADDED -> "$role ($roleId) was added to $user ($userId)"
      RoleReportType.ROLE_REMOVED -> "$role ($roleId) was removed from $user ($userId)"
    }

    channel.send(
      title = type.text,
      color = type.color,
      description = msg
    )
  }
}

interface UserReportMessage : ReportMessage {
  override val type: UserReportType
  val userId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val user = channel.server.getMemberById(userId).value?.name ?: "Not Found"
    val msg = when (type) {
      UserReportType.USER_JOINED -> "$user ($userId has joined the server."
      UserReportType.USER_LEFT -> "$user ($userId has left the server."
    }

    channel.send(
      title = type.text,
      color = type.color,
      description = msg
    )
  }
}

interface MessageReportMessage : ReportMessage {
  override val type: MessageReportType
  val channelId: Long
  val messageId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val ch = channel.server.getTextChannelById(channelId).value
    if (ch == null) {
      channel.send("Channel not found.")
      return
    }

    val message = ch.getMessageById(messageId).join()
    if (message == null) {
      channel.send("Message not found.")
      return
    }

    channel.send(
      title = type.text,
      color = type.color,
      author = message.author,
      description = stringList(
        "**Message ID:** $messageId",
        "**Context:** ${message.content}"
      )
    )
  }
}

interface PunishmentReportMessage : ReportMessage {
  override val type: PunishmentReportType
  val fromId: Long
  val toId: Long
  val time: Long
  val reason: String

  override fun sendTo(channel: ServerTextChannel) {
    val from = channel.server.getMemberById(fromId).value?.discriminatedName ?: "Not Found"
    val to = channel.server.getMemberById(toId).value?.discriminatedName ?: "Not Found"

    channel.send(
      title = type.text,
      color = type.color,
      description = stringList(
        "**From:** $from ($fromId)",
        "**To:** $to ($toId)",
        "**For:** ${formattedTime(time)}",
        "**Reason:**: $reason"
      )
    )
  }
}

interface PunishmentCompletedReportMessage : ReportMessage {
  override val type: PunishmentCompletedReportType
  val userId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val user = channel.server.getMemberById(userId).value?.discriminatedName ?: "Not Found"
    val msg = when (type) {
      PunishmentCompletedReportType.USER_UN_BANNED -> "$user ($userId) has been unbanned."
      PunishmentCompletedReportType.USER_UN_MUTED -> "$user ($userId) has been unmuted."
    }

    channel.send(
      title = type.text,
      color = type.color,
      description = msg
    )
  }
}