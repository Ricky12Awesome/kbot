package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.concurrent.CompletableFuture

interface Command {
  val name: String
  val description: String
  val usage: Usage
  val aliases: List<String>
  val permission: PermissionType

  fun call(event: CommandEvent) = event.onEvent()
  fun CommandEvent.onEvent()
}

interface CommandMessage {
  fun TextChannel.send(command: Command): CompletableFuture<Message>
  fun call(channel: TextChannel, command: Command) = channel.send(command)
}

class CommandEvent(
  parent: MessageCreateEvent,
  val prefix: String,
  val server: Server,
  val args: List<String>
) : MessageCreateEvent by parent

class CommandException(val commandMessage: CommandMessage) : Exception()
class CommandHandler : MessageCreateListener {
  val commandAliases = mutableMapOf<String, String>()
  val commands = mutableMapOf<String, Command>()

  fun register(command: Command) {
    command.aliases.forEach { commandAliases[it] = command.name }
    commandAliases[command.name] = command.name
    commands[command.name] = command
  }

  fun registerAll(vararg commands: Command) = commands.forEach(::register)

  private fun MessageCreateEvent.onEvent() {
    val prefix = "!"
    val server = if (server.isPresent) server.get() else return
    val args = message.content.split(" ")
    val aliases = commandAliases[args[0].removePrefix(prefix)] ?: return
    val command = commands[aliases] ?: return
    val event = CommandEvent(this, prefix, server, args)

    try {
      command.call(event)
    } catch (throwable: Throwable) {
      handleException(command, throwable)
    }

  }

  private fun MessageCreateEvent.handleException(command: Command, throwable: Throwable) = when (throwable) {
    is CommandException -> throwable.commandMessage.call(channel, command)
    else -> channel.sendMessage("")
  }

  override fun onMessageCreate(event: MessageCreateEvent) = event.onEvent()

}

class UsageArgument(val args: List<String>, val isRequired: Boolean)
class Usage(from: List<UsageArgument> = listOf()) : List<UsageArgument> by from {
  override fun toString(): String = joinToString(" ") {
    val args = it.args.joinToString(" | ")
    if (it.isRequired) "[$args]" else "<$args>"
  }

}

fun usage(vararg args: UsageArgument) = Usage(args.toList())

fun Command.required(vararg args: String) = UsageArgument(args.toList(), true)
fun Command.optional(vararg args: String) = UsageArgument(args.toList(), false)

