package me.ricky.discord.bot.kbot.util

import com.google.gson.Gson
import javafx.scene.paint.Color
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageAuthor
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.permission.Role
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User
import java.util.*

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
fun java.awt.Color.convert(): Color = Color.rgb(red, green, blue, alpha / 255.0)

fun java.awt.Color.rgb(): String = "$red/$green/$blue"
fun Color.rgb(): String = convert().rgb()

inline fun <reified T> Gson.fromJson(json: String) = fromJson(json, T::class.java)

val <T> Optional<T>.value: T? get() = if (isPresent) get() else null
val MessageAuthor.user get() = asUser().get()

fun <T> throwException(message: T, throwable: Throwable?) {
  throwable?.printStackTrace()
}

fun TextChannel.send(message: String): Message =
  sendMessage(message).join()

fun TextChannel.send(message: EmbedBuilder): Message =
  sendMessage(message).join()

fun User.toMember(server: Server): Member = MemberDelegate(
  delegate = this,
  server = server,
  roles = getRoles(server),
  roleColor = getRoleColor(server).value?.convert() ?: Color.TRANSPARENT
)

fun Member.toSQL() = SQLMember(this)
fun Server.toSQL() = SQLServer(this)

fun Server.getMember(id: Long): Member? = getMemberById(id).value?.toMember(this)
fun Server.getSQLMember(id: Long): SQLMember? = getMember(id)?.toSQL()

fun Server.rolesContainingName(name: String): List<Role> = roles.filter {
  if (it.name.toLowerCase() == name.toLowerCase()) return listOf(it)
  it.name.toLowerCase().contains(name.toLowerCase())
}

fun Role.info() = embed(
  title = "Role Info",
  color = color.value?.convert(),
  fields = listOf(
    inlineField("Members", "${users.size}"),
    inlineField("Color (R/G/B)", "${color.value?.rgb()}"),
    inlineField("ID", "$id"),
    inlineField("Mention", mentionTag)
  )
)

fun SQLMember.info() = sqlSelectFirst {
  val handler = sqlServer.xp
  embed(
    title = "User Info",
    thumbnailUrl = avatarUrl,
    color = roleColor,
    fields = listOf(
      inlineField("Id", "$id"),
      inlineField("XP", "${it[xp]}"),
      inlineField("Levels", "${handler.xpToLevel(it[xp])}"),
      inlineField("XP Needed", "${handler.xpForNextLevel(it[xp])}"),
      inlineField("Currency", "${it[currency]}"), // TODO: Get name from server
      inlineField("Mutes", "${it[mutes]}"),
      inlineField("Kicks", "${it[kicks]}"),
      inlineField("Bans", "${it[bans]}"),
      inlineField("Created on", "$creationTimestamp"),
      inlineField("Joined on", "${getJoinedAtTimestamp(server).value}")
    )
  )
}