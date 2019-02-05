package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.startTime
import me.ricky.discord.bot.kbot.time

class UptimeCommand : Command {
  override val name: String = "uptime"
  override val usage: Usage = usage(
    runAt(exact(0, 1)),
    optional("fake-uptime")
  )

  override fun CommandEvent.onEvent() {
    val time = when(runAt) {
      0 -> time(System.currentTimeMillis() - startTime)
      1 -> time((args[1].toLongOrNull() ?: throw exception("Invalid fake-uptime")) * 1000)
      else -> "None"
    }
    channel.sendMessage("Uptime: $time")
  }

}