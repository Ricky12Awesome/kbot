package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.util.SQLUser
import me.ricky.discord.bot.kbot.util.UserTable
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun Pair<User, Server>.sql() = sql(UserTable,
  { UserTable.userId eq first.id and (UserTable.serverId eq second.id) }) {
  SQLUser(serverId = it[serverId],
    userId = it[userId])
}

inline fun <T : Table, R> sql(
  table: T,
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline returns: T.(ResultRow) -> R
) = transaction { table.returns(table.select { where(table) }.first()) }
