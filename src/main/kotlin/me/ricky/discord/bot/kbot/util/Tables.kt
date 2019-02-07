package me.ricky.discord.bot.kbot.util

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.statements.InsertStatement

interface Insert<T> {
  fun insert(t: T): InsertStatement<Number>
}

object ServerTable : Table("server_table") {
  val serverId = long("server_id").primaryKey()
  val logChannelId = long("log_channel_id").default(0)
  val commandChannelId = long("command_channel_id").default(0)
  val xpScalar = double("xp_scalar").default(0.0)
  val currencyName = text("currency_name").default("Money")
  val currencySymbol = text("currency_symbol").default("$")
  val prefix = varchar("prefix", 3).default("!")

  fun createIfNotExists(serverId: Long) = insertIgnore { it[this.serverId] = serverId }

}

object UserTable : Table("user_table") {
  val userId = long("user_id").primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val mutes = long("mutes").default(0)
  val bans = long("bans").default(0)
  val kicks = long("kicks").default(0)
  val xp = double("xp").default(0.0)
  val reports = integer("reports").default(0)
  val currency = double("currency").default(0.0)

  fun createIfNotExists(userId: Long, serverId: Long) = insertIgnore {
    it[this.userId] = userId
    it[this.serverId] = serverId
  }
}

object ReportTable : Insert<ReportReportMessage>, Table("report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val reportedUserId = long("reported_user_id") references UserTable.userId
  val reason = text("reason")
  val count = integer("count")

  override fun insert(t: ReportReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[reportedUserId] = t.reportedUserId
    it[reason] = t.reason
    it[count] = t.count
  }
}

object RoleReportTable : Insert<RoleReportMessage>, Table("role_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val roleId = long("role_id")
  val userId = long("user_id") references UserTable.userId
  val type = enumeration("type", RoleReportType::class)
  val count = integer("count")

  override fun insert(t: RoleReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[roleId] = t.roleId
    it[userId] = t.userId
    it[type] = t.type
    it[count] = t.count
  }
}

object UserReportTable : Insert<UserReportMessage>, Table("user_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val userId = long("user_id") references UserTable.userId
  val type = enumeration("type", UserReportType::class)
  val count = integer("count")

  override fun insert(t: UserReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[userId] = t.userId
    it[type] = t.type
    it[count] = t.count
  }
}

object MessageReportTable : Insert<MessageReportMessage>, Table("message_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", MessageReportType::class)
  val channelId = long("channel_id")
  val messageId = long("message_id")
  val count = integer("count")

  override fun insert(t: MessageReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[type] = t.type
    it[channelId] = t.channelId
    it[messageId] = t.messageId
    it[count] = t.count
  }
}

object PunishmentReportTable : Insert<PunishmentReportMessage>, Table("punishment_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val fromId = long("from_id") references UserTable.userId
  val toId = long("to_id") references UserTable.userId
  val type = enumeration("type", PunishmentReportType::class)
  val time = long("time")
  val reason = text("reason")
  val count = integer("count")

  override fun insert(t: PunishmentReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[fromId] = t.fromId
    it[toId] = t.toId
    it[type] = t.type
    it[time] = t.time
    it[reason] = t.reason
    it[count] = t.count
  }
}

object PunishmentCompletedReportTable : Insert<PunishmentCompletedReportMessage>,
  Table("punishment_completed_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val userId = long("user_id") references UserTable.userId
  val type = enumeration("type", PunishmentCompletedReportType::class)
  val count = integer("count")

  override fun insert(t: PunishmentCompletedReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[userId] = t.userId
    it[type] = t.type
    it[count] = t.count
  }
}