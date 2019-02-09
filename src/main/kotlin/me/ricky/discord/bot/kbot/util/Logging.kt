package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.ServerTextChannel

enum class RoleReportType(override val text: String, override val color: Color) : ReportType {
  ROLE_ADDED("Role Added", Color.MEDIUMPURPLE),
  ROLE_REMOVED("Role Removed", Color.PURPLE),
}

enum class MemberReportType(override val text: String, override val color: Color) : ReportType {
  JOINED("Joined", Color.CYAN),
  LEFT("Left", Color.DARKCYAN)
}

enum class MessageReportType(override val text: String, override val color: Color) : ReportType {
  MESSAGE_SENT("Sent", Color.GRAY),
  MESSAGE_EDITED("Edited", Color.DARKGRAY),
  MESSAGE_CLEARED("Cleared", Color.WHITE)
}

enum class PunishmentReportType(override val text: String, override val color: Color) : ReportType {
  BANNED("Banned", Color.RED),
  KICKED("Kicked", Color.ORANGE),
  MUTED("Muted", Color.YELLOW)
}

enum class PunishmentCompletedReportType(override val text: String, override val color: Color) : ReportType {
  UN_BANNED("Unbanned", Color.LIME),
  UN_MUTED("Unmuted", Color.GREEN)
}

interface ReportType {
  val text: String
  val color: Color
}

interface ReportMessage {
  val type: ReportType
  val serverId: Long

  fun sendTo(channel: ServerTextChannel)
}

interface ReportReportMessage : ReportMessage {
  override val type: ReportType
    get() = object : ReportType {
      override val text: String = "Report"
      override val color: Color = Color.LIGHTBLUE
    }
  val reportedUserId: Long
  val reason: String

  override fun sendTo(channel: ServerTextChannel) {
    val member = channel.server.getMember(reportedUserId)?.discriminatedName ?: "Not Found"

    channel.send(
      title = type.text,
      color = type.color,
      description = "$member ($reportedUserId) was reported for `$reason`"
    )
  }
}

interface RoleReportMessage : ReportMessage {
  override val type: RoleReportType
  val roleId: Long
  val memberId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val role = channel.server.getRoleById(roleId).value?.name ?: "Not Found"
    val member = channel.server.getMember(memberId)?.discriminatedName ?: "Not Found"
    val msg = when (type) {
      RoleReportType.ROLE_ADDED -> "$role ($roleId) was added to $member ($memberId)"
      RoleReportType.ROLE_REMOVED -> "$role ($roleId) was removed from $member ($memberId)"
    }

    channel.send(
      title = type.text,
      color = type.color,
      description = msg
    )
  }
}

interface MemberReportMessage : ReportMessage {
  override val type: MemberReportType
  val memberId: Long

  override fun sendTo(channel: ServerTextChannel) {
    val member = channel.server.getMember(memberId)?.discriminatedName ?: "Not Found"
    val msg = when (type) {
      MemberReportType.JOINED -> "$member ($memberId has joined the server."
      MemberReportType.LEFT -> "$member ($memberId has left the server."
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
      author = message.author.user,
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
    val from = channel.server.getMember(fromId)?.discriminatedName ?: "Not Found"
    val to = channel.server.getMember(toId)?.discriminatedName ?: "Not Found"

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