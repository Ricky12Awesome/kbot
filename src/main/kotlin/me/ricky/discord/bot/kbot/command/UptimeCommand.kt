package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.startTime
import me.ricky.discord.bot.kbot.util.convert
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.shortFormattedTime

class UptimeCommand : Command {
  override val name: String = "uptime"
  override val usage: Usage = usage(runAt(exact(0), exactOrAfter(1)), optional("fake-uptime")
  )

  override fun CommandEvent.onEvent() {
    val time = when (runAt) {
      0 -> shortFormattedTime(System.currentTimeMillis() - startTime)
      1 -> shortFormattedTime(convert((args.slice(1..args.lastIndex).joinToString(""))))
      else -> "None"
    }
    channel.send("Uptime: $time")
  }

}