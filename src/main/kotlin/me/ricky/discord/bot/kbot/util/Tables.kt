package me.ricky.discord.bot.kbot.util

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.statements.InsertStatement

interface Insert<T> {
  fun insert(t: T): InsertStatement<*>
}

object ServerTable : Insert<ServerData>, Table("server_table") {
  val serverId = long("server_id").primaryKey()
  val logChannelId = long("log_channel_id").default(0)
  val commandChannelId = long("command_channel_id").default(0)
  val muteRoleId = long("mute_role_id").default(0)
  val xpScalar = double("xp_scalar").default(1.0)
  val currencyName = text("currency_name").default("Money")
  val currencySymbol = varchar("currency_symbol", 1).default("$")
  val prefix = varchar("prefix", 3).default("!")

  fun createIfNotExists(serverId: Long) = insertIgnore {
    it[this.serverId] = serverId
  }

  override fun insert(t: ServerData): InsertStatement<Long> = insertIgnore {
    it[serverId] = t.serverId
    it[logChannelId] = t.logChannelId
    it[commandChannelId] = t.commandChannelId
    it[muteRoleId] = t.muteRoleId
    it[xpScalar] = t.xpScalar
    it[currencyName] = t.currencyName
    it[currencySymbol] = t.currencySymbol
    it[prefix] = t.prefix
  }

}

object MemberTable : Insert<MemberData>, Table("member_table") {
  val memberId = long("member_id").primaryKey(0)
  val serverId = long("server_id").primaryKey(1) references ServerTable.serverId
  val mutes = integer("mutes").default(0)
  val bans = integer("bans").default(0)
  val kicks = integer("kicks").default(0)
  val xp = double("xp").default(0.0)
  val reports = integer("reports").default(0)
  val currency = double("currency").default(0.0)

  fun createIfNotExists(memberId: Long, serverId: Long) = insertIgnore {
    it[this.serverId] = serverId
    it[this.memberId] = memberId
  }

  override fun insert(t: MemberData): InsertStatement<Long> = insertIgnore {
    it[serverId] = t.serverId
    it[memberId] = t.memberId
    it[mutes] = t.mutes
    it[bans] = t.bans
    it[kicks] = t.kicks
    it[xp] = t.xp
    it[reports] = t.reports
    it[currency] = t.currency
  }
}

object ReportTable : Insert<ReportReportMessage>, Table("report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val reportedUserId = long("reported_user_id") references MemberTable.memberId
  val reason = text("reason")

  override fun insert(t: ReportReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[reportedUserId] = t.reportedUserId
    it[reason] = t.reason
  }
}

object RoleReportTable : Insert<RoleReportMessage>, Table("role_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val roleId = long("role_id")
  val userId = long("member_id") references MemberTable.memberId
  val type = enumeration("type", RoleReportType::class)

  override fun insert(t: RoleReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[roleId] = t.roleId
    it[userId] = t.memberId
    it[type] = t.type
  }
}

object MemberReportTable : Insert<MemberReportMessage>, Table("member_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val userId = long("member_id") references MemberTable.memberId
  val type = enumeration("type", MemberReportType::class)

  override fun insert(t: MemberReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[userId] = t.memberId
    it[type] = t.type
  }
}

object MessageReportTable : Insert<MessageReportMessage>, Table("message_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val type = enumeration("type", MessageReportType::class)
  val channelId = long("channel_id")
  val messageId = long("message_id")

  override fun insert(t: MessageReportMessage): InsertStatement<Number> = insert {
    it[serverId] = t.serverId
    it[type] = t.type
    it[channelId] = t.channelId
    it[messageId] = t.messageId
  }
}

object PunishmentReportTable : Insert<PunishmentReportMessage>, Table("punishment_report_table") {
  val reportId = long("report_id").autoIncrement().primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
  val fromId = long("from_id") references MemberTable.memberId
  val toId = long("to_id") references MemberTable.memberId
  val type = enumeration("type", PunishmentReportType::class)
  val isCompleted = bool("is_completed").default(false)
  val startTime = long("start_time")
  val time = long("time")
  val reason = text("reason")

  override fun insert(t: PunishmentReportMessage): InsertStatement<Number> = insert {
    it[startTime] = System.currentTimeMillis()
    it[serverId] = t.serverId
    it[fromId] = t.fromId
    it[toId] = t.toId
    it[type] = t.type
    it[time] = t.time
    it[reason] = t.reason
  }
}