package me.ricky.discord.bot.kbot.util

import org.javacord.api.DiscordApi
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.util.event.ListenerManager
import java.util.concurrent.TimeUnit

class MessageResponse<T, R>(
  val id: Long,
  val time: Pair<Long, TimeUnit> = 5L to TimeUnit.SECONDS,
  val api: DiscordApi,
  val get: MessageCreateEvent.() -> T,
  val call: MessageCreateEvent.(T) -> R,
  val condition: MessageCreateEvent.() -> Boolean = { true },
  val handle: (Throwable) -> Unit = { throw it }
) : MessageCreateListener {
  private lateinit var manager: ListenerManager<MessageCreateListener>

  fun execute() {
    manager = api.addMessageCreateListener(this).removeAfter(time.first, time.second)
  }

  override fun onMessageCreate(event: MessageCreateEvent) = with(event) {
    try {
      if (messageAuthor.id == id && condition()) {
        call(get())
        manager.remove()
      }
    } catch (t: Throwable) {
      handle(t)
    }
  }
}