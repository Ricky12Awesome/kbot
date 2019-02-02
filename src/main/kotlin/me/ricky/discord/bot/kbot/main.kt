package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color
import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.command.CommandEvent
import me.ricky.discord.bot.kbot.command.CommandHandler
import me.ricky.discord.bot.kbot.command.Usage
import me.ricky.discord.bot.kbot.command.exact
import me.ricky.discord.bot.kbot.command.exactOrAfter
import me.ricky.discord.bot.kbot.command.optional
import me.ricky.discord.bot.kbot.command.required
import me.ricky.discord.bot.kbot.command.runAt
import me.ricky.discord.bot.kbot.command.usage
import me.ricky.discord.bot.kbot.command.usageMessage
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType
import java.time.Instant

fun main(args: Array<String>) {
  val api = DiscordApiBuilder().setToken(System.getenv("TOKEN")).login().join()
  val commandHandler = CommandHandler()

  commandHandler.register(Test())

  api.addMessageCreateListener(commandHandler)
}

class Test : Command {
  override val name: String = "test"
  override val description: String = "A Simple test command for stuffs"
  override val aliases: List<String> = listOf("t", "testing", "?")
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage(
    runAt(
      exact(2),
      exactOrAfter(4)
    ),
    required("a1", "a2", "a3"),
    required("b1", "b2", "b3"),
    optional(
      required("c1", "c2"),
      required("d1")
    ))


  override fun CommandEvent.onEvent() {
    channel.sendMessage("Usage of this command is `$usage`")
    channel.sendMessage(
      footer = footer(usageMessage()),
      thumbnailUrl = user.avatarUrl,
      timestamp = Instant.now(),
      description = description,
      author = messageAuthor,
      color = Color.PURPLE,
      title = name,
      fields = listOf(
        inlinedField("Aliases", aliases.joinToString(", ", "[", "]")),
        inlinedField("Permission", permission.name),
        inlinedField("Usage", "$prefix$name $usage"),
        inlinedField("Examples", stringList(
          "Not Enough Args = $prefix$name a",
          "Correct = $prefix$name a b",
          "Correct = $prefix$name a b c d",
          "Not Enough Args = $prefix$name a b c"
        ))
      )
    )
  }
}