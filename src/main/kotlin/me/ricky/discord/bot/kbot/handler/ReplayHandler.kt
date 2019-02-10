package me.ricky.discord.bot.kbot.handler

import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

data class Replay<T, R>(
  val get: MessageCreateEvent.() -> T,
  val callback: MessageCreateEvent.(T) -> R,
  val condition: MessageCreateEvent.() -> Boolean = { true },
  val handle: (Throwable) -> Unit = { throw it }
) {
  val call: MessageCreateEvent.() -> R = { callback(get()) }
}

class ReplayHandler : MessageCreateListener {
  val replays = mutableMapOf<Long, Replay<*, *>>()

  fun <T, R> register(
    id: Long,
    time: Pair<Long, TimeUnit>,
    get: MessageCreateEvent.() -> T,
    callback: MessageCreateEvent.(T) -> R,
    condition: MessageCreateEvent.() -> Boolean = { true },
    handle: (Throwable) -> Unit = { throw it }
  ) {
    timer(initialDelay = time.second.toMillis(time.first), period = 1000) {
      replays.remove(id)
      cancel()
    }
    replays[id] = Replay(get, callback, condition, handle)
  }

  override fun onMessageCreate(event: MessageCreateEvent) {
    val replay = replays[event.messageAuthor.id] ?: return
    event.handle(event.messageAuthor.id, replay)
    replays.remove(event.messageAuthor.id)
  }

  fun MessageCreateEvent.handle(id: Long, replay: Replay<*, *>) = with(replay) {
    try {
      if (messageAuthor.id == id && condition()) call()
    } catch (t: Throwable) {
      handle(t)
    }
  }
}