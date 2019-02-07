package me.ricky.discord.bot.kbot.util

import org.jetbrains.exposed.sql.Table

object ServerTable : Table("server_table") {
  val serverId = long("server_id").primaryKey()
  val logChannelId = long("log_channel_id").default(0)
  val commandChannelId = long("command_channel_id").default(0)
  val xpScalar = double("xp_scalar").default(0.0)
  val currencyName = text("currency_name").default("Money")
  val currencySymbol = text("currency_symbol").default("$")
  val prefix = varchar("prefix", 3)
}

object UserTable : Table("user_table") {
  val userId = long("user_id").primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val mutes = long("mutes").default(0)
  val bans = long("bans").default(0)
  val kicks = long("kicks").default(0)
  val xp = double("xp").default(0.0)
  val currency = double("currency").default(0.0)
}

object ReportTable : Table("report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val reportedUserId = long("reported_user_id")
}

object RoleReportTable : Table("role_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", RoleReportType::class)
  val roleId = long("role_id")
  val userId = long("user_id")
}

object UserReportTable : Table("user_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", UserReportType::class)
  val userId = long("user_id")
}

object MessageReportTable : Table("message_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", MessageReportType::class)
  val channelId = long("channel_id")
  val messageId = long("message_id")
}

object PunishmentReportTable : Table("punishment_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", PunishmentReportType::class)
  val fromId = long("from_id")
  val toId = long("to_id")
  val time = long("time")
  val reason = text("reason")
}

object PunishmentCompletedReportTable : Table("punishment_completed_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", PunishmentCompletedReportType::class)
  val userId = long("user_id")
}