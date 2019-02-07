package me.ricky.discord.bot.kbot.util

data class ServerData(
  val serverId: Long = 0,
  val logChannelId: Long = 0,
  val commandChannelId: Long = 0,
  val xpScalar: Double = 0.0,
  val currencyName: String = "",
  val currencySymbol: String = "",
  val prefix: String = ""
)

data class MemberData(
  val memberId: Long = 0,
  val serverId: Long = 0,
  val mutes: Int = 0,
  val bans: Int = 0,
  val kicks: Int = 0,
  val xp: Double = 0.0,
  val reports: Int = 0,
  val currency: Double = 0.0
)

data class Report(
  override val serverId: Long,
  override val reportedUserId: Long,
  override val reason: String
) : ReportReportMessage

data class RoleReport(
  override val type: RoleReportType,
  override val serverId: Long,
  override val roleId: Long,
  override val memberId: Long
) : RoleReportMessage

data class MemberReport(
  override val type: MemberReportType,
  override val serverId: Long,
  override val memberId: Long
) : MemberReportMessage

data class MessageReport(
  override val type: MessageReportType,
  override val serverId: Long,
  override val channelId: Long,
  override val messageId: Long
) : MessageReportMessage

data class PunishmentReport(
  override val type: PunishmentReportType,
  override val serverId: Long,
  override val fromId: Long,
  override val toId: Long,
  override val time: Long,
  override val reason: String
) : PunishmentReportMessage

data class PunishmentCompleteReport(
  override val type: PunishmentCompletedReportType,
  override val serverId: Long,
  override val memberId: Long
) : PunishmentCompletedReportMessage
