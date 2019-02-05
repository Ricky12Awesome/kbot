package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.startTime
import me.ricky.discord.bot.kbot.util.shortFormattedTime

class UptimeCommand : Command {
  override val name: String = "uptime"
  override val usage: Usage = usage(
    runAt(exact(0, 1)),
    optional("fake-uptime")
  )

  override fun CommandEvent.onEvent() {
    val time = when (runAt) {
      0 -> shortFormattedTime(System.currentTimeMillis() - startTime)
      1 -> shortFormattedTime((args[1].toLongOrNull()
        ?: throw exception("Invalid fake-uptime")) * 1000)
      else -> "None"
    }
    channel.sendMessage("Uptime: $time")
  }

}