package me.ricky.discord.bot.kbot.util

data class Report(
  override val reportedUserId: Long,
  override val reason: String
) : ReportReportMessage

data class RoleReport(
  override val type: RoleReportType,
  override val roleId: Long,
  override val userId: Long
) : RoleMessage

data class UserReport(
  override val type: UserReportType,
  override val userId: Long
) : UserReportMessage

data class MessageReport(
  override val type: MessageReportType,
  override val channelId: Long,
  override val messageId: Long
) : MessageReportMessage

data class PunishmentReport(
  override val type: PunishmentReportType,
  override val fromId: Long,
  override val toId: Long,
  override val time: Long,
  override val reason: String
) : PunishmentReportMessage

data class PunishmentCompleteReport(
  override val type: PunishmentCompletedReportType,
  override val userId: Long
) : PunishmentCompletedReportMessage
