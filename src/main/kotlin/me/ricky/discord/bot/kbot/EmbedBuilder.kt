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

typealias Footer = Pair<String, String?>

/**
 * @param text footer text
 * @param iconUrl footer icon url
 *
 * @return [Footer]
 */
fun footer(text: String, iconUrl: String? = null): Footer = text to iconUrl

/**
 * @param name Name of Field
 * @param value Value of Field
 * @param isInlined If the field should be inline or not
 *
 * @see inlineField
 *
 * @return [EmbedField]
 */
fun field(name: String, value: String, isInlined: Boolean = false): EmbedField = EmbedFieldImpl(name, value, isInlined)

/**
 *
 * @param name Name of Field
 * @param value Value of Field
 *
 * @see field
 *
 * @return an inlined [field]
 */
fun inlineField(name: String, value: String): EmbedField = field(name, value, true)

/**
 * Works exactly the same as [embed] but returns [TextChannel.sendMessage] with [embed] passed in.
 *
 * @see embed
 * @see TextChannel.sendMessage
 */
fun TextChannel.sendMessage(
  from: EmbedBuilder = EmbedBuilder(),
  author: MessageAuthor? = null,
  thumbnailUrl: String? = null,
  description: String? = null,
  timestamp: Instant? = null,
  imageUrl: String? = null,
  footer: Footer? = null,
  title: String? = null,
  color: Color? = null,
  url: String? = null,
  fields: List<EmbedField> = listOf(),
  apply: EmbedBuilder.() -> Unit = {}
): CompletableFuture<Message> = sendMessage(
  embed(from, author, thumbnailUrl, description, timestamp, imageUrl, footer, title, color, url, fields, apply)
)

/**
 *
 * @param from Used to expand upon an instance of an [EmbedBuilder] default value is EmbedBuilder()
 * @param author sets the author using [EmbedBuilder.setAuthor]
 * @param thumbnailUrl sets the thumbnail url using [EmbedBuilder.setThumbnail]
 * @param description sets the description using [EmbedBuilder.setDescription]
 * @param timestamp sets the timestamp using [EmbedBuilder.setTimestamp]
 * @param imageUrl sets the image url using [EmbedBuilder.setTitle]
 * @param footer sets the [Footer] using [EmbedBuilder.setFooter]
 * @param title sets the title using [EmbedBuilder.setTitle]
 * @param color sets the [Color] and converts it so it can be used for [EmbedBuilder.setColor]
 * @param url sets the url using [EmbedBuilder.setUrl]
 * @param fields takes these list of fields and adds them using [EmbedBuilder.addField]
 * @param apply apply's code to [EmbedBuilder] just if you where to do [EmbedBuilder.apply].
 * This is called before any other param is called
 *
 * @see sendMessage
 *
 * @throws IllegalStateException if any values are blank.
 * as in if they're like `title = ""` not if it's like `title = null`
 */
inline fun embed(
  from: EmbedBuilder = EmbedBuilder(),
  author: MessageAuthor? = null,
  thumbnailUrl: String? = null,
  description: String? = null,
  timestamp: Instant? = null,
  imageUrl: String? = null,
  footer: Footer? = null,
  title: String? = null,
  color: Color? = null,
  url: String? = null,
  fields: List<EmbedField> = listOf(),
  apply: EmbedBuilder.() -> Unit = {}
) = from.apply(apply).apply {
  if (author != null) setAuthor(author)
  setThumbnail(thumbnailUrl)
  setDescription(description)
  setTimestamp(timestamp)
  setColor(color?.convert())
  setImage(imageUrl)
  setTitle(title)
  setUrl(url)

  setFooter(footer?.first, footer?.second)

  fields.forEach { addField(it.name, it.value, it.isInline) }
}