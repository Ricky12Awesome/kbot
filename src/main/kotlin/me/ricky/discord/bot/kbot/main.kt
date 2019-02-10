package me.ricky.discord.bot.kbot

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.command.`fun`.SayCommand
import me.ricky.discord.bot.kbot.command.information.ChangeLogCommand
import me.ricky.discord.bot.kbot.command.information.GitHubCommand
import me.ricky.discord.bot.kbot.command.information.HelpCommand
import me.ricky.discord.bot.kbot.command.information.InfoCommand
import me.ricky.discord.bot.kbot.command.information.ReportsCommand
import me.ricky.discord.bot.kbot.command.information.RoleInfoCommand
import me.ricky.discord.bot.kbot.command.information.StatsCommand
import me.ricky.discord.bot.kbot.command.information.UptimeCommand
import me.ricky.discord.bot.kbot.command.information.UserInfoCommand
import me.ricky.discord.bot.kbot.command.management.SettingsCommand
import me.ricky.discord.bot.kbot.command.moderation.MuteCommand
import me.ricky.discord.bot.kbot.command.moderation.PurgeCommand
import me.ricky.discord.bot.kbot.command.moderation.ReportCommand
import me.ricky.discord.bot.kbot.command.other.XPCommand
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.CommandHandler
import me.ricky.discord.bot.kbot.handler.PunishmentHandler
import me.ricky.discord.bot.kbot.handler.ReplayHandler
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.NekoLife
import me.ricky.discord.bot.kbot.util.database.MemberReportTable
import me.ricky.discord.bot.kbot.util.database.MemberTable
import me.ricky.discord.bot.kbot.util.database.MessageReportTable
import me.ricky.discord.bot.kbot.util.database.PunishmentReportTable
import me.ricky.discord.bot.kbot.util.database.ReportTable
import me.ricky.discord.bot.kbot.util.database.RoleReportTable
import me.ricky.discord.bot.kbot.util.database.ServerTable
import me.ricky.discord.bot.kbot.util.database.sql
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManager
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.sql.DriverManager

const val COMMAND_PAGE = "https://github.com/Ricky12Awesome/kbot#commands"
const val GITHUB_PAGE = "https://github.com/Ricky12Awesome/kbot"
const val ISSUE_PAGE = "https://github.com/Ricky12Awesome/kbot/issues"
const val CHANGE_LOG = "https://github.com/Ricky12Awesome/kbot#change-log"
const val VERSION = "0.3.0"
val startTime: Long = System.currentTimeMillis()
val gson: Gson = GsonBuilder().setPrettyPrinting().create()

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
      MemberTable,
      ReportTable,
      RoleReportTable,
      MemberReportTable,
      MessageReportTable,
      PunishmentReportTable
    )
  }

  val commandHandler = CommandHandler()
  val punishmentHandler = PunishmentHandler(api)
  val replayHandler = ReplayHandler()

  commandHandler.registerAll(
    Test(),
    XPCommand(),
    SayCommand(),
    PurgeCommand(),
    StatsCommand(),
    UptimeCommand(),
    GitHubCommand(),
    ReportCommand(),
    ReportsCommand(),
    UserInfoCommand(),
    RoleInfoCommand(),
    ChangeLogCommand(),
    SettingsCommand(replayHandler),
    MuteCommand(punishmentHandler),
    InfoCommand(commandHandler.commands.size),
    HelpCommand(commandHandler.commands)
  )

  api.addMessageCreateListener(replayHandler)
  api.addMessageCreateListener(commandHandler)
}

class Test : Command {
  override val name: String = "test"
  override val description: String = "A Simple test command for stuffs"
  override val aliases: List<String> = listOf("t", "testing")
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(exact(1))

  override fun CommandEvent.onEvent() {
    val type = NekoLife.endpoints.find { it == args[1] } ?: throw exception("Invalid endpoints")
    val response = NekoLife.getResponse(type)

    if(response.isNSFW && !channel.isNsfw) throw exception("Sorry, but you can only do that in NSFW Channels")

    channel.send(
      imageUrl = response.url
    )
  }
}