package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import java.util.concurrent.CompletableFuture

val notEnoughArgs = exception("Not Enough Arguments")
val tooManyArgs = exception("Too Many Arguments")

inline fun message(crossinline run: SendFunction) = object : CommandMessage {
  override fun TextChannel.send(command: Command): CompletableFuture<Message> = run(command)
}

fun exception(msg: String) = CommandException(message { sendMessage(msg) })
inline fun exception(crossinline run: SendFunction) = CommandException(message(run))