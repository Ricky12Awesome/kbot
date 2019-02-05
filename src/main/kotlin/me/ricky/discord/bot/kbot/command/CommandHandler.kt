package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

/**
 * An event for when a command is run
 *
 * @param parent instance of [MessageCreateEvent] to inherit
 * @param prefix prefix of the command
 * @param server server of the command
 * @param user  user who ran the command
 * @param runAt what index was the command ran at from [Usage.runsAt]
 * @param args arguments of the command
 */
data class CommandEvent(
  val parent: MessageCreateEvent,
  val prefix: String,
  val server: Server,
  val user: User,
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
    val prefix = "!"
    val user = if (message.userAuthor.isPresent) message.userAuthor.get() else return
    val server = if (server.isPresent) server.get() else return
    val args = message.content.split(" ")
    val aliases = commandAliases[args[0].removePrefix(prefix)] ?: return
    val command = commands[aliases] ?: return
    if (args.getOrNull(1) == "-h") {
      channel.sendMessage(command.help())
      return
    }

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
