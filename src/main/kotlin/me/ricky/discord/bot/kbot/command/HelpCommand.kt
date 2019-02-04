package me.ricky.discord.bot.kbot.command

import javafx.scene.paint.Color
import me.ricky.discord.bot.kbot.GITHUB_PAGE_COMMANDS
import me.ricky.discord.bot.kbot.embed
import me.ricky.discord.bot.kbot.footer
import me.ricky.discord.bot.kbot.inlineField
import me.ricky.discord.bot.kbot.sendMessage

class HelpCommand(val commands: Map<String, Command>) : Command {
  override val name: String = "help"
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
    channel.sendMessage("Please visit <$GITHUB_PAGE_COMMANDS> for a list of commands.")
  }

  fun CommandEvent.showHelpForCommand() {
    val command = commands[args[1]] ?: throw exception("$name doesn't exist. do ${prefix}help for commands")
    channel.sendMessage(command.help())
  }

  fun Command.help() = embed(
    title = name,
    color = Color.LIME,
    description = description,
    footer = footer(usageMessage()),
    fields = listOf(
      inlineField("Aliases", "$aliases"),
      inlineField("Usage", "$usage"),
      inlineField("Permission", "$permission")
    )
  )
}