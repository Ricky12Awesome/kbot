package me.ricky.discord.bot.kbot.util

import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

inline fun <R> Server.sql(
  crossinline returns: ServerTable.(ResultRow) -> R
) = sql(ServerTable, { it.serverId eq id }, returns)

inline fun <R> User.sql(
  crossinline returns: UserTable.(ResultRow) -> R
) = sql(UserTable, { it.serverId eq id }, returns)

inline fun <T : Table, R> sql(
  table: T,
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline returns: T.(ResultRow) -> R
): CompletableFuture<R> = CompletableFuture.supplyAsync {
  transaction {
    table.returns(table.select { where(table) }.first())
  }
}