package me.ricky.discord.bot.kbot.command

import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.server.Server
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

enum class UsageType { OPTIONAL, REQUIRED }
enum class CommandExceptionType { NO_PERMISSION, DISABLED_DM, OTHER }

interface Command {
    val name: String
    val description: String
    val usage: List<UsageArgument>
    val aliases: List<String>
    val permission: PermissionType

    fun call(event: CommandEvent) = event.onEvent()
    fun CommandEvent.onEvent()
}

class CommandEvent(parent: MessageCreateEvent, val server: Server, val args: List<String>) : MessageCreateEvent by parent
class CommandException(val type: CommandExceptionType, val msg: String) : Exception()
class CommandExceptionEmbed(val type: CommandExceptionType, val msg: EmbedBuilder) : Exception()

class CommandHandler : MessageCreateListener {
    val commands = mutableMapOf<String, Command>()

    private fun MessageCreateEvent.onEvent() {
        val server = if (server.isPresent) server.get() else return
        val args = message.content.split(" ")
        val command = commands[args[0].removePrefix("!")] ?: return
        val event = CommandEvent(this, server, args)
        try {
            command.call(event)
        } catch (e: Throwable) {
            when (e) {
                is CommandException -> channel.sendMessage(onCommandException(command, e))
                is CommandExceptionEmbed -> channel.sendMessage(onCommandExceptionEmbed(command, e))
                else -> channel.sendMessage("An unknown error has occurred.")
            }
        }
    }

    private fun MessageCreateEvent.onCommandException(command: Command, e: CommandException) = when (e.type) {
        CommandExceptionType.NO_PERMISSION -> "You do not have permission `${command.permission}`."
        CommandExceptionType.DISABLED_DM -> "You need to enable DM's so the bot can message you."
        CommandExceptionType.OTHER -> e.msg
    }

    private fun MessageCreateEvent.onCommandExceptionEmbed(command: Command, e: CommandExceptionEmbed) = when (e.type) {
        CommandExceptionType.NO_PERMISSION -> noPermission(command)
        CommandExceptionType.DISABLED_DM -> dmDisabled(command)
        CommandExceptionType.OTHER -> e.msg
    }

    override fun onMessageCreate(event: MessageCreateEvent) = event.onEvent()

}

class UsageArgument(val args: List<String>, val type: UsageType)

class UsageBuilder : MutableList<UsageArgument> by mutableListOf() {
    fun required(vararg args: String) = apply { add(UsageArgument(args.toList(), UsageType.REQUIRED)) }
    fun optional(vararg args: String) = apply { add(UsageArgument(args.toList(), UsageType.OPTIONAL)) }
}

fun MessageCreateEvent.noPermission(command: Command) = EmbedBuilder().apply {
    setAuthor(messageAuthor)
    addField(":no_entry: No Permission", "You do not have permission `${command.permission}`")
}

fun MessageCreateEvent.dmDisabled(command: Command) = EmbedBuilder().apply {
    setAuthor(messageAuthor)
    addField(":no_entry: DM's Disabled", "You need to enable DM's so the bot can message you")
}

fun Command.usage() = usage.joinToString(" ") {
    val args = it.args.joinToString(" | ")
    when (it.type) {
        UsageType.REQUIRED -> "[$args]"
        UsageType.OPTIONAL -> "<$args>"
    }
}

inline fun usage(builder: UsageBuilder.() -> Unit): List<UsageArgument> = UsageBuilder().apply(builder)

fun Command.required(vararg args: String) = UsageArgument(args.toList(), UsageType.REQUIRED)
fun Command.optional(vararg args: String) = UsageArgument(args.toList(), UsageType.OPTIONAL)

