package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.CHANGE_LOG
import me.ricky.discord.bot.kbot.GITHUB_PAGE
import me.ricky.discord.bot.kbot.ISSUE_PAGE
import me.ricky.discord.bot.kbot.VERSION
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.startTime
import me.ricky.discord.bot.kbot.util.avatarUrl
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.shortFormattedTime

class InfoCommand(val commandCount: Int) : Command {
  override val name: String = "info"
  override val description: String = "Gives info about the bot"

  override fun CommandEvent.onEvent() {
    channel.send(
      title = "> Info",
      description = "A Basic bot as of right now.",
      thumbnailUrl = api.yourself.avatarUrl,
      fields = listOf(
        inlineField("Author", api.owner.join().name),
        inlineField("Version", VERSION),
        inlineField("Language", "Kotlin"),
        inlineField("Discord API", "JavaCord"),
        inlineField("Commands", "$commandCount"),
        inlineField("Servers In", "${api.servers.size}"),
        inlineField("Server Prefix", prefix),
        inlineField("Uptime", shortFormattedTime(time = System.currentTimeMillis() - startTime)),
        inlineField("GitHub", GITHUB_PAGE),
        inlineField("Change Log", CHANGE_LOG),
        inlineField("Report Bugs", ISSUE_PAGE)
      )
    )
  }

}