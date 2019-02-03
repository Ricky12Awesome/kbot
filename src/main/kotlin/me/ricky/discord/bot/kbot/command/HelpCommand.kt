package me.ricky.discord.bot.kbot.command

import javafx.scene.paint.Color
import me.ricky.discord.bot.kbot.embed
import me.ricky.discord.bot.kbot.footer
import me.ricky.discord.bot.kbot.inlineField

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
    channel.sendMessage(commands.keys.joinToString())
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