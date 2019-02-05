package me.ricky.discord.bot.kbot.database

import org.jetbrains.exposed.sql.Table

object ServerTable : Table("server_table") {
  val serverId = long("server_id").primaryKey()
  val logChannelId = long("log_channel_id").default(0)
  val commandChannelId = long("command_channel_id").default(0)
  val prefix = varchar("prefix", 3)
}

data class Server(
  val serverId: Long,
  val logChannelId: Long,
  val commandChannelId: Long,
  val prefix: String
)

object UserTable : Table("user_table") {
  val userId = long("user_id").primaryKey()
  val serverId = long("server_id") references ServerTable.serverId
}

data class SQLUser(
  val userId: Long,
  val serverId: Long
)