package me.ricky.discord.bot.kbot.database

import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Pair<User, Server>.sql() = sql(UserTable, { it.userId eq first.id and (it.serverId eq second.id) }) {
  SQLUser(serverId = it[serverId], userId = it[userId])
}

inline fun <T : Table, R> sql(
  table: T,
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline returns: T.(ResultRow) -> R
) = transaction { table.returns(table.select { where(table) }.first()) }
