package me.ricky.discord.bot.kbot

import me.ricky.discord.bot.kbot.command.*
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.permission.PermissionType

val TOKEN: String = System.getenv("TOKEN")

fun main(args: Array<String>) {
    val api = DiscordApiBuilder().setToken(TOKEN).login().join()
    val commandHandler = CommandHandler()

    commandHandler.commands["test"] = Test()

    api.addMessageCreateListener(commandHandler)

}

class Test : Command {
    override val name: String = "test"
    override val description: String = "none"
    override val usage: List<UsageArgument> = listOf(
        required("user", "user-id"),
        optional("amount")
    )
    override val aliases: List<String> = listOf()
    override val permission: PermissionType = PermissionType.ADMINISTRATOR

    override fun CommandEvent.onEvent() {
        channel.sendMessage("The usage of this command is ${usage()}")
    }
}