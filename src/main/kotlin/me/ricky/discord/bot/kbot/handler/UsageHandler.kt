package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.command.Command
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import java.util.concurrent.CompletableFuture

typealias RunAt = Pair<Int, RunAtType>
typealias SendFunction = TextChannel.(command: Command) -> CompletableFuture<Message>

/**
 * @param prefix the prefix
 * @param prefix the suffix
 *
 * @property both [prefix] + [suffix]
 * @property pair [prefix] to [suffix]
 */
enum class UsageType(val prefix: String, val suffix: String) {
  OPTIONAL("[", "]"),
  REQUIRED("<", ">"),
  NONE("", "");

  val both = prefix + suffix
  val pair = prefix to suffix
}

/**
 * used for Usage checking
 * @see [Usage]
 * @see [Command.canRun]
 */
enum class RunAtType {
  EXACT, AFTER, EXACT_AFTER;

  fun calc(index: Int, lastIndex: Int) = when (this) {
    EXACT -> index == lastIndex
    AFTER -> index < lastIndex
    EXACT_AFTER -> index <= lastIndex
  }
}

/**
 * TODO: Document Usage Argument
 */

data class UsageArgument(
  val args: List<String>,
  val type: UsageType,
  val separator: String = " | ",
  val formatted: String = args.joinToString(separator)
) {
  fun format(): String = type.prefix + formatted + type.suffix
}

/**
 * TODO: Document Usage
 */
class Usage(from: List<UsageArgument> = listOf(), val runsAt: List<RunAt> = listOf()) : List<UsageArgument> by from {
  override fun toString(): String {
    val result = joinToString(" ") { it.format() }

    return if (result.isBlank()) "none" else result
  }
}