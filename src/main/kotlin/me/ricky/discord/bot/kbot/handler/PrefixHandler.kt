package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.tag
import me.ricky.discord.bot.kbot.util.toSQL
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

class PrefixHandler : MessageCreateListener {

  override fun onMessageCreate(event: MessageCreateEvent) = with(event) {
    val server = server.value?.toSQL() ?: return

    if (message.content == api.yourself.tag) {
      channel.send("The prefix is `${server.data.prefix}`")
    }
  }
}