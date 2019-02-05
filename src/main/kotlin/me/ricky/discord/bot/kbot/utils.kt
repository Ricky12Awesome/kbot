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

fun msToSecond(num: Number) = num.toLong() / 1000
fun msToMinute(num: Number) = msToSecond(num) / 60
fun msToHour(num: Number) = msToMinute(num) / 60
fun msToDay(num: Number) = msToHour(num) / 24
val Number.length get() = (Math.floor(Math.log10(toDouble())) + 1).toInt()

fun time(time: Long): String = buildString {
  val seconds = msToSecond(time) - msToMinute(time) * 60
  val minutes = msToMinute(time) - msToHour(time) * 60
  val hours = msToHour(time) - msToDay(time) * 24
  val days = msToDay(time)

  append(if (days.length == 2) "$days" else "0$days:")
  append(if (hours.length == 2) "$hours:" else "0$hours:")
  append(if (minutes.length == 2) "$minutes:" else "0$minutes:")
  append(if (seconds.length == 2) "$seconds" else "0$seconds")
}