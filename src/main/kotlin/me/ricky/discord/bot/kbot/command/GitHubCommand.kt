package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.GITHUB_PAGE
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.util.send

class GitHubCommand : Command {
  override val name: String = "github"
  override val aliases: List<String> = listOf("git")
  override val description: String = "Links you to $GITHUB_PAGE"

  override fun CommandEvent.onEvent() {
    channel.send("Here's the link to the github page. $GITHUB_PAGE")
  }

}