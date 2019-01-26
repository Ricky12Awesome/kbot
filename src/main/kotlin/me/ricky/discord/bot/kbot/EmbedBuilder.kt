package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageAuthor
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.message.embed.EmbedField
import org.javacord.core.entity.message.embed.EmbedFieldImpl
import java.time.Instant
import java.util.concurrent.CompletableFuture

fun field(name: String, value: String, isInlined: Boolean = false) = EmbedFieldImpl(name, value, isInlined)
fun check(value: Boolean, throwMessage: String = "undefined") =
  if (value) true else throw IllegalStateException(throwMessage)

fun EmbedBuilder.checkNotBlank(str: String?, name: String) =
  if (str != null) check(str.isNotBlank(), "$name cannot be blank!") else false

fun TextChannel.sendEmbedMessage(
  from: EmbedBuilder = EmbedBuilder(),
  author: MessageAuthor? = null,
  thumbnailUrl: String? = null,
  description: String? = null,
  timestamp: Instant? = null,
  imageUrl: String? = null,
  title: String? = null,
  color: Color? = null,
  url: String? = null,
  fields: List<EmbedField> = listOf(),
  apply: EmbedBuilder.() -> Unit = {}
): CompletableFuture<Message> =
  sendMessage(embed(from, author, thumbnailUrl, description, timestamp, imageUrl, title, color, url, fields, apply))

inline fun embed(
  from: EmbedBuilder = EmbedBuilder(),
  author: MessageAuthor? = null,
  thumbnailUrl: String? = null,
  description: String? = null,
  timestamp: Instant? = null,
  imageUrl: String? = null,
  title: String? = null,
  color: Color? = null,
  url: String? = null,
  fields: List<EmbedField> = listOf(),
  apply: EmbedBuilder.() -> Unit = {}
) = from.apply(apply).apply {
  if (checkNotBlank(thumbnailUrl, "ThumbnailUrl")) setThumbnail(thumbnailUrl)
  if (checkNotBlank(description, "Description")) setDescription(description)
  if (checkNotBlank(imageUrl, "ImageUrl")) setImage(imageUrl)
  if (checkNotBlank(title, "Title")) setTitle(title)
  if (checkNotBlank(url, "Url")) setUrl(url)

  if (timestamp != null) setTimestamp(timestamp)
  if (color != null) setColor(color.convert())
  if (author != null) setAuthor(author)

  fields.forEach {
    checkNotBlank(it.name, "Name")
    checkNotBlank(it.value, "Value")

    addField(it.name, it.value, it.isInline)
  }
}
