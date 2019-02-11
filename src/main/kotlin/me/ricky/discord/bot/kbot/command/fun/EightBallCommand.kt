package me.ricky.discord.bot.kbot.command.`fun`

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandCategory
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType
import kotlin.random.Random

class EightBallCommand : Command {
  override val name: String = "8ball"
  override val description: String = "Answers a yes or no question"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val category: CommandCategory = CommandCategory.FUN
  override val usage: Usage = usage(exactOrAfter(1), required("question"))

  override fun CommandEvent.onEvent() {
    val question = args.slice(1..args.lastIndex).joinToString(" ")
    channel.send(
      fields = listOf(
        inlineField("Question", question),
        inlineField("Answer ", answer())
      )
    )
  }

  private fun answer(): String = when(Random.nextInt(4)) {
    0 -> "Yes."
    1 -> "No."
    2 -> "Maybe?"
    3 -> "How should I know?"
    else -> ""
  }
}