package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.user.User
import java.util.*
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

val <T> Optional<T>.value: T? get() = if (isPresent) get() else null

fun <T> throwException(message: T, throwable: Throwable?) = if (throwable != null) throw throwable else Unit

fun TextChannel.send(message: String): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)

fun TextChannel.send(message: EmbedBuilder): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)

fun msToSecond(num: Number) = num.toLong() / 1000
fun msToMinute(num: Number) = msToSecond(num) / 60
fun msToHour(num: Number) = msToMinute(num) / 60
fun msToDay(num: Number) = msToHour(num) / 24
val Number.length get() = (Math.floor(Math.log10(toDouble())) + 1).toInt()
val Number.plural get() = if (toDouble() > 1) "s" else ""

data class TimeData(
  val ms: Long = 0,
  val seconds: Long = msToSecond(ms) - msToMinute(ms) * 60,
  val minutes: Long = msToMinute(ms) - msToHour(ms) * 60,
  val hours: Long = msToHour(ms) - msToDay(ms) * 24,
  val days: Long = msToDay(ms)
)

fun time(time: Long): String = buildString {
  val (_, seconds, minutes, hours, days) = TimeData(time)

  append(if (days.length == 2) "$days" else "0$days:")
  append(if (hours.length == 2) "$hours:" else "0$hours:")
  append(if (minutes.length == 2) "$minutes:" else "0$minutes:")
  append(if (seconds.length == 2) "$seconds" else "0$seconds")
}

fun shortFormattedTime(time: Long) = formattedTime(
  time = time,
  pluralize = false,
  useGrammar = false,
  day = "d",
  hour = "h",
  minute = "m",
  second = "s"
)

fun formattedTime(
  time: Long,
  pluralize: Boolean = true,
  useGrammar: Boolean = true,
  day: String = " day",
  hour: String = " hour",
  minute: String = " minute",
  second: String = " second"
): String {
  val (_, seconds, minutes, hours, days) = TimeData(time)
  val list = mutableListOf<String>()

  if (days > 0) list += "$days$day${if (pluralize) days.plural else ""}"
  if (hours > 0) list += "$hours$hour${if (pluralize) hours.plural else ""}"
  if (minutes > 0) list += "$minutes$minute${if (pluralize) minutes.plural else ""}"
  if (seconds > 0) list += "$seconds$second${if (pluralize) seconds.plural else ""}"

  if (useGrammar) for (i in 0 until list.lastIndex) {
    list[i] += if (i == list.lastIndex - 1) " and" else ","
  }


  return list.joinToString(" ")
}