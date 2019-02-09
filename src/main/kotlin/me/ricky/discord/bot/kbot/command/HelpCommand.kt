package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.COMMAND_PAGE
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.send

class HelpCommand(val commands: Map<String, Command>) : Command {
  override val name: String = "help"
  override val aliases: List<String> = listOf("?")
  override val description: String = "Help Command"
  override val usage: Usage = usage(
    runAt(exact(0, 1)),
    optional("command-name")
  )

  override fun CommandEvent.onEvent() {
    when (runAt) {
      0 -> listCommandNames()
      1 -> showHelpForCommand()
    }
  }

  fun CommandEvent.listCommandNames() {
    channel.send("Please visit <$COMMAND_PAGE> for a list of commands.")
  }

  fun CommandEvent.showHelpForCommand() {
    val command = commands[args[1]] ?: throw exception("$name doesn't exist. do ${prefix}help for commands")
    channel.send(command.help())
  }
}