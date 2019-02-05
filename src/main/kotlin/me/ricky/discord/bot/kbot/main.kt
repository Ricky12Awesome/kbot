package me.ricky.discord.bot.kbot

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.command.CommandEvent
import me.ricky.discord.bot.kbot.command.CommandHandler
import me.ricky.discord.bot.kbot.command.HelpCommand
import me.ricky.discord.bot.kbot.command.InfoCommand
import me.ricky.discord.bot.kbot.command.PurgeCommand
import me.ricky.discord.bot.kbot.command.SayCommand
import me.ricky.discord.bot.kbot.command.UptimeCommand
import me.ricky.discord.bot.kbot.command.Usage
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType

const val GITHUB_PAGE = "https://github.com/Ricky12Awesome/kbot"
const val GITHUB_PAGE_COMMANDS = "https://github.com/Ricky12Awesome/kbot#Commands"
val startTime: Long = System.currentTimeMillis()

fun main(args: Array<String>) {
  val api = DiscordApiBuilder().setToken(System.getenv("TOKEN")).login().join()
  val commandHandler = CommandHandler()

  commandHandler.registerAll(
    Test(),
    SayCommand(),
    PurgeCommand(),
    UptimeCommand(),
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
    runAt(
      exactOrAfter(2, 4)
    ),
    required("a1", "a2", "a3"),
    required("b1", "b2", "b3"),
    optional(
      required("c1", "c2"),
      required("d1...")
    ))


  override fun CommandEvent.onEvent() {
    val embed = embed(fields = listOf(field(" ", " ")))

    channel.sendAsync(embed)
  }
}