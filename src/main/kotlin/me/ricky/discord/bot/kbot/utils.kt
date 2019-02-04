package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.user.User
import java.util.concurrent.CompletableFuture

/**
 * short hand for avatar.url.toString()
 */
val User.avatarUrl get() = avatar.url.toString()

/**
 * @param string string to be used in the list
 * @param separator what separates the list of strings into a single string
 */
fun stringList(vararg string: String, separator: String = "\n") = string.joinToString(separator)

/**
 * converts [javafx.scene.paint.Color] to [java.awt.Color]
 */
fun Color.convert(): java.awt.Color = java.awt.Color(
  red.toFloat(),
  green.toFloat(),
  blue.toFloat(),
  opacity.toFloat()
)

fun <T> throwException(message: T, throwable: Throwable?) = if (throwable != null) throw throwable else Unit

fun TextChannel.send(message: String): Message = sendMessage(message).join()
fun TextChannel.send(message: EmbedBuilder): Message = sendMessage(message).join()
fun TextChannel.sendAsync(message: String): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)

fun TextChannel.sendAsync(message: EmbedBuilder): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)