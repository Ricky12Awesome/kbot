package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.Server
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

/**
 * converts [javafx.scene.paint.Color] to [java.awt.Color]
 */
fun java.awt.Color.convert(): Color = Color.rgb(
  red,
  green,
  blue,
  alpha / 255.0
)

val <T> Optional<T>.value: T? get() = if (isPresent) get() else null

fun <T> throwException(message: T, throwable: Throwable?) = if (throwable != null) throw throwable else Unit

fun TextChannel.send(message: String): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)

fun TextChannel.send(message: EmbedBuilder): CompletableFuture<Message> =
  sendMessage(message).whenComplete(::throwException)

fun User.toMember(server: Server): Member = MemberData(
  delegate = this,
  server = server,
  roles = getRoles(server),
  roleColor = getRoleColor(server).value?.convert() ?: Color.TRANSPARENT
)

fun Server.getMember(id: Long): Member? = getMemberById(id).value?.toMember(this)

fun Member.info() = sql(server.id) {
  embed(
    title = "User Info",
    thumbnailUrl = avatarUrl,
    color = roleColor,
    fields = listOf(
      inlineField("Id", "$id"),
      inlineField("Xp", "${it[xp]}"),
      inlineField("Currency", "${it[currency]}"), // TODO: Get name from server
      inlineField("Mutes", "${it[mutes]}"),
      inlineField("Kicks", "${it[kicks]}"),
      inlineField("Bans", "${it[bans]}"),
      inlineField("Created on", "$creationTimestamp"),
      inlineField("Joined on", "${getJoinedAtTimestamp(server).value}")
    )
  )
}