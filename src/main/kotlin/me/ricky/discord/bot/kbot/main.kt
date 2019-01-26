package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color
import me.ricky.discord.bot.kbot.command.*
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType

fun main(args: Array<String>) {
  val api = DiscordApiBuilder().setToken(System.getenv("TOKEN")).login().join()
  val commandHandler = CommandHandler()

  commandHandler.register(Test())

  api.addMessageCreateListener(commandHandler)

}

class Test : Command {
  override val name: String = "test"
  override val description: String = "A Simple test command for stuffs"
  override val usage: Usage = usage(
    required("user", "user-id"),
    optional("amount")
  )
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR

  override fun CommandEvent.onEvent() {

    channel.sendEmbedMessage(
      description = description,
      author = messageAuthor,
      color = Color.PURPLE,
      title = name,
      fields = listOf(
        field("Aliases", aliases.joinToString(", ", "[", "]")),
        field("Usage", "$prefix$name $usage"),
        field("Permission", permission.name)
      )
    )

  }
}