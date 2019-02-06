package me.ricky.discord.bot.kbot

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.command.GitHubCommand
import me.ricky.discord.bot.kbot.command.HelpCommand
import me.ricky.discord.bot.kbot.command.InfoCommand
import me.ricky.discord.bot.kbot.command.PurgeCommand
import me.ricky.discord.bot.kbot.command.SayCommand
import me.ricky.discord.bot.kbot.command.StatsCommand
import me.ricky.discord.bot.kbot.command.UptimeCommand
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.CommandHandler
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.UserTable
import me.ricky.discord.bot.kbot.util.convert
import me.ricky.discord.bot.kbot.util.formattedTime
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType
import org.jetbrains.exposed.sql.select
import kotlin.reflect.KProperty

const val GITHUB_PAGE = "https://github.com/Ricky12Awesome/kbot"
const val COMMAND_PAGE = "https://github.com/Ricky12Awesome/kbot#Commands"
const val ISSUE_PAGE = "https://github.com/Ricky12Awesome/kbot/issues"
const val VERSION = "ALPHA-Stage"
val startTime: Long = System.currentTimeMillis()

fun main(args: Array<String>) {
  val api = DiscordApiBuilder().setToken(System.getenv("TOKEN")).login().join()
  val commandHandler = CommandHandler()

  commandHandler.registerAll(
    Test(),
    SayCommand(),
    PurgeCommand(),
    UptimeCommand(),
    StatsCommand(),
    GitHubCommand(),
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
  override val usage: Usage = usage(
    runAt(exactOrAfter(1)),
    required("user-reportId"),
    required("time"),
    required("reason...")
  )

  override fun CommandEvent.onEvent() {
    val time = args.slice(1..args.lastIndex).joinToString("")

    channel.send(formattedTime(convert(time)))
  }
}