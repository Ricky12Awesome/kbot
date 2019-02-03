package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.command.RunAtType.AFTER
import me.ricky.discord.bot.kbot.command.RunAtType.EXACT
import me.ricky.discord.bot.kbot.command.RunAtType.EXACT_AFTER
import me.ricky.discord.bot.kbot.command.UsageType.OPTIONAL
import me.ricky.discord.bot.kbot.command.UsageType.REQUIRED
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.concurrent.CompletableFuture

typealias RunAt = Pair<Int, RunAtType>
typealias SendFunction = TextChannel.(command: Command) -> CompletableFuture<Message>

/**
 * @param prefix prefix
 * @param prefix suffix
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
 * @property name Name of the command
 * @property description Description of the command. default = "undefined"
 * @property usage Usage of the command. default = [usage]
 * @property aliases Aliases of the command. default = [emptyList]
 * @property permission Permission of the command. default = [PermissionType.SEND_MESSAGES]
 */
interface Command {
  val name: String
  val description: String get() = "undefined"
  val usage: Usage get() = usage()
  val aliases: List<String> get() = emptyList()
  val permission: PermissionType get() = PermissionType.SEND_MESSAGES

  /**
   * @param event used to call [CommandEvent.onEvent]
   */
  fun call(event: CommandEvent) = event.onEvent()

  /**
   * Runs when a command event happens
   */
  fun CommandEvent.onEvent()

  /**
   * Checks if what the user entered is correct usage
   *
   * @param last last index of argument
   */
  fun canRun(last: Int): Int {
    if (usage.isEmpty()) return 0

    usage.runsAt.forEach {
      val (index, type) = it
      if (type.calc(index, last)) return index
    }

    throw notEnoughArgs
  }

}

/**
 * TODO: Document Command Message
 */
interface CommandMessage {
  fun TextChannel.send(command: Command): CompletableFuture<Message>
  fun call(channel: TextChannel, command: Command) = channel.send(command)
}

/**
 * TODO: Document Simple Command
 */
class SimpleCommand(
  override val name: String,
  override val description: String = "undefined",
  override val usage: Usage = usage(),
  override val aliases: List<String> = listOf(),
  override val permission: PermissionType = PermissionType.SEND_MESSAGES,
  val event: CommandEvent.() -> Unit = { }
) : Command {
  override fun CommandEvent.onEvent() = event()
}

/**
 * TODO: Document Command Event
 */
data class CommandEvent(
  val parent: MessageCreateEvent,
  val prefix: String,
  val server: Server,
  val user: User,
  val runAt: Int,
  val args: List<String>
) : MessageCreateEvent by parent {

  fun at(index: Int) = args at index
  fun atOrAfter(index: Int) = args at index

  infix fun List<String>.at(index: Int) = lastIndex == index
  infix fun List<String>.atOrAfter(index: Int) = lastIndex >= index

  operator fun List<String>.compareTo(index: Int): Int = lastIndex.compareTo(index)
}

/**
 * TODO: Document Command Exception
 */
class CommandException(val commandMessage: CommandMessage) : Exception()

/**
 * TODO: Document Command Handler
 */
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
    val user = if (message.userAuthor.isPresent) message.userAuthor.get() else return
    val server = if (server.isPresent) server.get() else return
    val args = message.content.split(" ")
    val aliases = commandAliases[args[0].removePrefix(prefix)] ?: return
    val command = commands[aliases] ?: return

    try {
      val runAt = command.canRun(args.lastIndex)
      val event = CommandEvent(this, prefix, server, user, runAt, args)
      command.call(event)
    } catch (throwable: Throwable) {
      handleException(command, throwable)
    }
  }

  private fun MessageCreateEvent.handleException(command: Command, throwable: Throwable) = when (throwable) {
    is CommandException -> throwable.commandMessage.call(channel, command)
    else -> {
      throwable.printStackTrace()
      channel.sendMessage("An unknown exception has occurred")
    }
  }

  override fun onMessageCreate(event: MessageCreateEvent) = event.onEvent()
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

class Usage(from: List<UsageArgument> = listOf(), val runsAt: List<RunAt> = listOf()) : List<UsageArgument> by from {
  override fun toString(): String = joinToString(" ") { it.format() }
}

/**
 * TODO: Document command
 */
fun command(
  name: String,
  description: String = "undefined",
  usage: Usage = usage(),
  aliases: List<String> = listOf(),
  permission: PermissionType = PermissionType.SEND_MESSAGES,
  event: CommandEvent.() -> Unit = { }
) = SimpleCommand(name, description, usage, aliases, permission, event)

/**
 * TODO: Document usage
 */
fun usage(runsAt: List<RunAt> = listOf(), vararg args: UsageArgument) = Usage(args.toList(), runsAt)

/**
 * TODO: Document usage message
 */
fun usageMessage() = buildString {
  append("`${UsageType.REQUIRED.both}` = Required, ")
  append("`${UsageType.OPTIONAL.both}` = Optional")
}

/**
 * TODO: Document required
 */
fun required(vararg args: String) = UsageArgument(args.toList(), REQUIRED)

/**
 * TODO: Document optional
 */
fun optional(vararg args: String) = UsageArgument(args.toList(), OPTIONAL)

/**
 * TODO: Document required
 */
fun required(vararg args: UsageArgument) = UsageArgument(args.map { it.format() }, REQUIRED)

/**
 * TODO: Document optional
 */
fun optional(vararg args: UsageArgument) = UsageArgument(args.map { it.format() }, OPTIONAL)

/**
 * TODO: Document run at
 */
fun runAt(vararg args: List<RunAt>) = args.flatMap { it }

/**
 * TODO: Document exact
 */
fun exact(vararg args: Int) = args.map { it to EXACT }

/**
 * TODO: Document after
 */
fun after(vararg args: Int) = args.map { it to AFTER }

/**
 * TODO: Document exact or after
 */
fun exactOrAfter(vararg args: Int) = args.map { it to EXACT_AFTER }
