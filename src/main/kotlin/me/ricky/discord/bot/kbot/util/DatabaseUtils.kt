package me.ricky.discord.bot.kbot.util

import org.javacord.api.entity.server.Server
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.concurrent.CompletableFuture

interface SQLData<T> {
  val data: T
}

interface SQLObject<T : Table> {
  val table: T
  val where: SqlExpressionBuilder.(T) -> Op<Boolean>
  val createIfNotExists: T.() -> Unit
}

class SQLServer(from: Server) : Server by from, SQLData<ServerData>, SQLObject<ServerTable> {
  override val table: ServerTable = ServerTable
  override val where: SqlExpressionBuilder.(ServerTable) -> Op<Boolean> = { it.serverId eq id }
  override val createIfNotExists: ServerTable.() -> Unit = { createIfNotExists(id) }
  override val data: ServerData = sqlSelectFirst {
    ServerData(
      commandChannelId = it[commandChannelId],
      currencySymbol = it[currencySymbol],
      logChannelId = it[logChannelId],
      currencyName = it[currencyName],
      serverId = it[serverId],
      xpScalar = it[xpScalar],
      prefix = it[prefix]
    )
  }.join()

  val logChannel = getTextChannelById(data.logChannelId).value
  val commandChannel = getTextChannelById(data.commandChannelId).value

}

class SQLMember(from: Member) : Member by from, SQLObject<MemberTable> {
  override val table: MemberTable = MemberTable
  override val where: SqlExpressionBuilder.(MemberTable) -> Op<Boolean> = { it.serverId eq serverId and (it.memberId eq id) }
  override val createIfNotExists: MemberTable.() -> Unit = { createIfNotExists(id, server.id) }
}

inline fun <T : Table> SQLObject<T>.sqlInsert(
  crossinline insert: T.(InsertStatement<Number>) -> Unit
) = sql { table.insert { insert(it) } }

inline fun <T : Table> SQLObject<T>.sqlInsertIgnore(
  crossinline insert: T.(UpdateBuilder<*>) -> Unit
) = sql { table.insertIgnore { insert(it) } }

inline fun <T : Table> SQLObject<T>.sqlUpdate(
  noinline where: SqlExpressionBuilder.(T) -> Op<Boolean> = this.where,
  noinline createIfNotExists: T.() -> Unit = this.createIfNotExists,
  crossinline update: T.(UpdateStatement) -> Unit
) = sql {
  table.createIfNotExists()
  table.update({ where(table) }) { update(it) }
}

inline fun <T : Table, R> SQLObject<T>.sqlSelectFirst(
  noinline where: SqlExpressionBuilder.(T) -> Op<Boolean> = this.where,
  noinline createIfNotExists: T.() -> Unit = this.createIfNotExists,
  crossinline returns: T.(ResultRow) -> R
) = sql {
  table.createIfNotExists()
  table.returns(table.select { where(table) }.first())
}

inline fun <T : Table> SQLObject<T>.sqlSelectAll(
  noinline where: SqlExpressionBuilder.(T) -> Op<Boolean> = this.where,
  noinline createIfNotExists: T.() -> Unit = this.createIfNotExists,
  crossinline forEach: T.(ResultRow) -> Unit
) = sql {
  table.createIfNotExists()
  table.select { where(table) }.forEach { table.forEach(it) }
}

inline fun <T : Table, R> SQLObject<T>.sqlSelect(
  noinline where: SqlExpressionBuilder.(T) -> Op<Boolean> = this.where,
  noinline createIfNotExists: T.() -> Unit = this.createIfNotExists,
  crossinline returns: T.(Query) -> R
) = sql {
  table.createIfNotExists()
  table.returns(table.select { where(table) })
}

@JvmName("sqlSelectUnit")
inline fun <reified T : Table> sqlSelect(
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline createIfNotExists: T.() -> Unit = {},
  crossinline returns: T.(Query) -> Unit
): CompletableFuture<Unit> = sqlSelect<T, Unit>(where, createIfNotExists, returns)

inline fun <reified T : Table, R> sqlSelect(
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline createIfNotExists: T.() -> Unit = {},
  crossinline returns: T.(Query) -> R
) = sql {
  val table = T::class.objectInstance ?: throw IllegalArgumentException("T Must be an Object.")
  table.createIfNotExists()
  table.returns(table.select { where(table) })
}

inline fun <reified T : Table> sqlUpdate(
  crossinline where: SqlExpressionBuilder.(T) -> Op<Boolean>,
  crossinline createIfNotExists: T.() -> Unit = {},
  crossinline update: T.(UpdateStatement) -> Unit
) = sql {
  val table = T::class.objectInstance ?: throw IllegalArgumentException("T Must be an Object.")
  table.createIfNotExists()
  table.update({ where(table) }) { update(it) }
}


inline fun <T> sql(crossinline transaction: Transaction.() -> T): CompletableFuture<T> =
  CompletableFuture.supplyAsync { transaction { transaction() } }.whenComplete(::throwException)