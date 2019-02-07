package me.ricky.discord.bot.kbot

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.command.GitHubCommand
import me.ricky.discord.bot.kbot.command.HelpCommand
import me.ricky.discord.bot.kbot.command.InfoCommand
import me.ricky.discord.bot.kbot.command.PurgeCommand
import me.ricky.discord.bot.kbot.command.ReportCommand
import me.ricky.discord.bot.kbot.command.ReportsCommand
import me.ricky.discord.bot.kbot.command.SayCommand
import me.ricky.discord.bot.kbot.command.StatsCommand
import me.ricky.discord.bot.kbot.command.UptimeCommand
import me.ricky.discord.bot.kbot.command.UserInfoCommand
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.CommandHandler
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.MessageReportTable
import me.ricky.discord.bot.kbot.util.PunishmentCompletedReportTable
import me.ricky.discord.bot.kbot.util.PunishmentReportTable
import me.ricky.discord.bot.kbot.util.ReportTable
import me.ricky.discord.bot.kbot.util.RoleReportTable
import me.ricky.discord.bot.kbot.util.ServerTable
import me.ricky.discord.bot.kbot.util.UserReportTable
import me.ricky.discord.bot.kbot.util.UserTable
import me.ricky.discord.bot.kbot.util.sql
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

const val COMMAND_PAGE = "https://github.com/Ricky12Awesome/kbot#Commands"
const val GITHUB_PAGE = "https://github.com/Ricky12Awesome/kbot"
const val ISSUE_PAGE = "https://github.com/Ricky12Awesome/kbot/issues"
const val VERSION = "ALPHA-Stage"
val startTime: Long = System.currentTimeMillis()

fun main(args: Array<String>) {
  val api = DiscordApiBuilder().setToken(System.getenv("TOKEN")).login().join()
  val path = Paths.get("assets", "database.db").apply {
    if (Files.notExists(parent)) Files.createDirectories(parent)
  }
  Database.connect({ DriverManager.getConnection("jdbc:sqlite:$path") }) {
    ThreadLocalTransactionManager(
      db = it,
      defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE,
      defaultRepetitionAttempts = Connection.TRANSACTION_SERIALIZABLE
    )
  }

  sql {
    SchemaUtils.createMissingTablesAndColumns(
      ServerTable,
      UserTable,
      ReportTable,
      RoleReportTable,
      UserReportTable,
      MessageReportTable,
      PunishmentReportTable,
      PunishmentCompletedReportTable
    )
  }

  val commandHandler = CommandHandler()

  commandHandler.registerAll(
    Test(),
    SayCommand(),
    PurgeCommand(),
    UptimeCommand(),
    StatsCommand(),
    GitHubCommand(),
    ReportCommand(),
    ReportsCommand(),
    UserInfoCommand(),
    InfoCommand(commandHandler.commands),
    HelpCommand(commandHandler.commands)
  )

  api.addMessageCreateListener(commandHandler)
}

class Test : Command {
  override val name: String = "test"
  override val description: String = "A Simple test command for stuffs"
  override val aliases: List<String> = listOf("t", "testing")
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage()

  override fun CommandEvent.onEvent() {

  }
}