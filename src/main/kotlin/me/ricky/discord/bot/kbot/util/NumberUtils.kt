package me.ricky.discord.bot.kbot.util

import java.util.concurrent.TimeUnit

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

fun convert(time: String): Long {
  var start = 0
  var result = 0L

  fun String.convert(start: Int, end: Int, result: (Long) -> Long) = result(slice(start..end).toLong())

  time.forEachIndexed { index, c ->
    val next = index - 1
    val last = index + 1
    result += when (c.toLowerCase()) {
      'd' -> time.convert(start.also { start = last }, next) { TimeUnit.DAYS.toMillis(it) }
      'h' -> time.convert(start.also { start = last }, next) { TimeUnit.HOURS.toMillis(it) }
      'm' -> time.convert(start.also { start = last }, next) { TimeUnit.MINUTES.toMillis(it) }
      's' -> time.convert(start.also { start = last }, next) { TimeUnit.SECONDS.toMillis(it) }
      else -> 0
    }
  }

  return result
}