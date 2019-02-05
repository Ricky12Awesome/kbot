package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.GITHUB_PAGE
import me.ricky.discord.bot.kbot.avatarUrl
import me.ricky.discord.bot.kbot.inlineField
import me.ricky.discord.bot.kbot.sendMessage
import me.ricky.discord.bot.kbot.startTime
import me.ricky.discord.bot.kbot.time

class InfoCommand(val commands: Map<String, Command>) : Command {
  override val name: String = "info"
  override val description: String = "Gives info about the bot"

  override fun CommandEvent.onEvent() {
    channel.sendMessage(
      title = "> Info",
      description = "A Basic bot as of right now.",
      thumbnailUrl = api.yourself.avatarUrl,
      fields = listOf(
        inlineField("Commands", "${commands.keys.size}"),
        inlineField("Author", api.owner.join().name),
        inlineField("Language", "Kotlin"),
        inlineField("Discord API", "JavaCord"),
        inlineField("Server Prefix", prefix),
        inlineField("Uptime", time(System.currentTimeMillis() - startTime)),
        inlineField("GitHub", GITHUB_PAGE)
      )
    )
  }

}