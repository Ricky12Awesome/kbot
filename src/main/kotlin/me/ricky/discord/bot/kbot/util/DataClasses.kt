package me.ricky.discord.bot.kbot.util

data class Report(
  override val serverId: Long,
  override val count: Int,
  override val reportedUserId: Long,
  override val reason: String
) : ReportReportMessage

data class RoleReport(
  override val type: RoleReportType,
  override val serverId: Long,
  override val count: Int,
  override val roleId: Long,
  override val userId: Long
) : RoleReportMessage

data class UserReport(
  override val type: UserReportType,
  override val serverId: Long,
  override val count: Int,
  override val userId: Long
) : UserReportMessage

data class MessageReport(
  override val type: MessageReportType,
  override val serverId: Long,
  override val count: Int,
  override val channelId: Long,
  override val messageId: Long
) : MessageReportMessage

data class PunishmentReport(
  override val type: PunishmentReportType,
  override val serverId: Long,
  override val count: Int,
  override val fromId: Long,
  override val toId: Long,
  override val time: Long,
  override val reason: String
) : PunishmentReportMessage

data class PunishmentCompleteReport(
  override val type: PunishmentCompletedReportType,
  override val serverId: Long,
  override val count: Int,
  override val userId: Long
) : PunishmentCompletedReportMessage
