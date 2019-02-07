package me.ricky.discord.bot.kbot.util

import org.javacord.api.entity.user.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

inline fun <T> User.sql(
  serverId: Long,
  crossinline returns: UserTable.(ResultRow) -> T
) = sql {
  UserTable.createIfNotExists(id, serverId)
  val query = UserTable.select { UserTable.serverId eq serverId and (UserTable.userId eq id) }
  UserTable.returns(query.first())
}

inline fun User.reports(
  serverId: Long,
  limit: Int,
  offset: Int,
  crossinline forEach: ReportTable.(ResultRow) -> Unit
) = sql {
  ReportTable.select {
    ReportTable.serverId eq serverId and (ReportTable.reportedUserId eq id)
  }.orderBy(ReportTable.reportId).limit(limit, offset).forEach { ReportTable.forEach(it) }
}


inline fun <T> sql(crossinline transaction: Transaction.() -> T): CompletableFuture<T> =
  CompletableFuture.supplyAsync { transaction { transaction() } }