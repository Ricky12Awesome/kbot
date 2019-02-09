package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.command.Command
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.event.message.MessageCreateEvent
import java.util.concurrent.CompletableFuture

val invalidArguments = exception("Invalid Arguments")

/**
 * TODO: Document Command Message
 */
interface CommandMessage {
  fun TextChannel.send(command: Command): CompletableFuture<Message>
  fun call(channel: TextChannel, command: Command) = channel.send(command)
}

fun MessageCreateEvent.handleException(command: Command, throwable: Throwable): CompletableFuture<Message> = when (throwable) {
  is CommandException -> throwable.commandMessage.call(channel, command)
  else -> {
    throwable.printStackTrace()
    channel.sendMessage("An unknown exception has occurred")
  }
}

fun CommandEvent.handleException(throwable: Throwable): CompletableFuture<Message> = when (throwable) {
  is CommandException -> throwable.commandMessage.call(channel, command)
  else -> {
    throwable.printStackTrace()
    channel.sendMessage("An unknown exception has occurred")
  }
}

/**
 * TODO: Document message
 */
inline fun message(crossinline run: SendFunction) = object :
  CommandMessage {
  override fun TextChannel.send(command: Command): CompletableFuture<Message> = run(command)
}

/**
 * TODO: Document exception
 */
fun exception(msg: String) =
  CommandException(message { sendMessage(msg) })

/**
 * TODO: Document exception
 */
inline fun exception(crossinline run: SendFunction) =
  CommandException(message(run))


/**
 * TODO: Document Command Exception
 */
class CommandException(val commandMessage: CommandMessage) : Exception()
