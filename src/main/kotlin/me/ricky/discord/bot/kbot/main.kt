package me.ricky.discord.bot.kbot

import org.javacord.api.DiscordApiBuilder

val TOKEN: String = System.getenv("TOKEN")

fun main(args: Array<String>) {
    val api = DiscordApiBuilder().setToken(TOKEN).login().join()


}