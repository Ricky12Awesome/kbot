package me.ricky.discord.bot.kbot.handler

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.util.XPLevelHandler
import me.ricky.discord.bot.kbot.util.database.SQLMember
import me.ricky.discord.bot.kbot.util.database.SQLServer
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.tag
import me.ricky.discord.bot.kbot.util.toMember
import me.ricky.discord.bot.kbot.util.toSQL
import me.ricky.discord.bot.kbot.util.user
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.concurrent.CompletableFuture

enum class CommandCategory {
  MODERATION, INFORMATION, MANAGEMENT, FUN, OTHER
}

/**
 * An event for when a command is call
 *
 * @param parent instance of [MessageCreateEvent] to inherit
 * @param prefix prefix of the command
 * @param server server of the command
 * @param member member who ran the command
 * @param runAt what index was the command ran at from [Usage.runsAt]
 * @param args arguments of the command
 */
data class CommandEvent(
  val parent: MessageCreateEvent,
  val prefix: String,
  val serverId: Long,
  val server: SQLServer,
  val command: Command,
  val channel: ServerTextChannel,
  val member: SQLMember,
  val xp: XPLevelHandler,
  val runAt: Int,
  val args: List<String>
) : MessageCreateEvent by parent

/**
 * @property commandAliases Holds the aliases for commands
 * @property commands Holds all the commands
 */
class CommandHandler : MessageCreateListener {
  val commandAliases = mutableMapOf<String, String>()
  val commands = mutableMapOf<String, Command>()

  /**
   * @param command command to register
   *
   * @see registerAll
   */
  fun register(command: Command) {
    command.aliases.forEach { commandAliases[it] = command.name }
    commandAliases[command.name] = command.name
    commands[command.name] = command
  }

  /**
   * @param commands commands to register
   */
  fun registerAll(vararg commands: Command) = commands.forEach(::register)

  private fun MessageCreateEvent.onEvent() {
    if (api.yourself == message.author.user) return
    val channel = channel as? ServerTextChannel ?: return
    val server = (server.value ?: return).toSQL()
    val user = (message.userAuthor.value ?: return).toMember(server).toSQL()
    val prefix = server.data.prefix
    val message = message.content.replaceFirst("${api.yourself.tag} ", prefix)
    val args = message.split(" ")
    val aliases = commandAliases[args[0].removePrefix(prefix)] ?: return
    val command = commands[aliases] ?: return
    if (!args[0].startsWith(prefix)) return
    if (args.getOrNull(1) == "-h") {
      channel.send(command.help())
      return
    }

    CompletableFuture.supplyAsync {
      val event = CommandEvent(
        parent = this,
        prefix = prefix,
        serverId = server.id,
        server = server,
        member = user,
        channel = channel,
        command = command,
        xp = server.xp,
        runAt = command.canRun(args.lastIndex),
        args = args
      )

      command.call(event)
    }.exceptionally { handleException(command, it.cause ?: it) }
  }

  override fun onMessageCreate(event: MessageCreateEvent) = event.onEvent()
}
