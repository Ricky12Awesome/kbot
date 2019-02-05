package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.user.UserStatus

class StatsCommand : Command {
  override val name: String = "stats"
  override val description: String = "Gives stats about the server"

  override fun CommandEvent.onEvent() {
    channel.send(
      title = server.name,
      thumbnailUrl = server.icon.get().url.toString(),
      fields = listOf(
        inlineField("Owner", server.owner.name),
        inlineField("Prefix", prefix),
        inlineField("Roles", "${server.roles.size}"),
        inlineField("Members", "${server.members.filter { !it.isBot }.size}"),
        inlineField("Bots", "${server.members.filter { it.isBot }.size}"),
        inlineField("Online", "${server.members.filter { it.status == UserStatus.ONLINE }.size}"),
        inlineField("Offline", "${server.members.filter { it.status == UserStatus.OFFLINE }.size}"),
        inlineField("Do Not Disturb", "${server.members.filter { it.status == UserStatus.DO_NOT_DISTURB }.size}"),
        inlineField("Away", "${server.members.filter { it.status == UserStatus.IDLE }.size}"),
        inlineField("Invisible", "${server.members.filter { it.status == UserStatus.INVISIBLE }.size}"),
        inlineField("Channels", "${server.channels.size}"),
        inlineField("Categories", "${server.channelCategories.size}"),
        inlineField("Voice Channels", "${server.voiceChannels.size}"),
        inlineField("Text Channels", "${server.textChannels.size}")
      )
    )
  }

}