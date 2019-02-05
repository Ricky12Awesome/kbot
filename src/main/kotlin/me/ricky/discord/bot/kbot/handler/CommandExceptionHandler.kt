package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.command.Command
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import java.util.concurrent.CompletableFuture

val notEnoughArgs = exception("Not Enough Arguments")
val tooManyArgs = exception("Too Many Arguments")

/**
 * TODO: Document Command Message
 */
interface CommandMessage {
  fun TextChannel.send(command: Command): CompletableFuture<Message>
  fun call(channel: TextChannel, command: Command) = channel.send(command)
}

class CommandExceptionHandler

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
