package me.ricky.discord.bot.kbot.util

import me.ricky.discord.bot.kbot.gson
import java.net.URL


fun test() {

}

object NekoLife {
  val isNSFW = mapOf(
    "femdom" to true,
    "tickle" to false,
    "classic" to true,
    "ngif" to false,
    "erofeet" to false,
    "meow" to false,
    "erok" to true,
    "poke" to false,
    "les" to true,
    "hololewd" to true,
    "lewdk" to true,
    "keta" to true,
    "feetg" to true,
    "nsfw_neko_gif" to true,
    "eroyuri" to true,
    "kiss" to false,
    "8ball" to false,
    "kuni" to true,
    "tits" to true,
    "pussy_jpg" to true,
    "cum_jpg" to true,
    "pussy" to true,
    "lewdkemo" to true,
    "lizard" to false,
    "slap" to false,
    "lewd" to true,
    "cum" to true,
    "cuddle" to false,
    "spank" to true,
    "smallboobs" to true,
    "goose" to false,
    "Random_hentai_gif" to true,
    "avatar" to false,
    "fox_girl" to false,
    "nsfw_avatar" to true,
    "hug" to false,
    "gecg" to false,
    "boobs" to true,
    "pat" to false,
    "feet" to true,
    "smug" to false,
    "kemonomimi" to false,
    "solog" to true,
    "holo" to false,
    "wallpaper" to true,
    "bj" to true,
    "woof" to false,
    "yuri" to true,
    "trap" to true,
    "anal" to true,
    "baka" to true,
    "blowjob" to true,
    "holoero" to true,
    "feed" to false,
    "neko" to false,
    "gasm" to false,
    "hentai" to true,
    "futanari" to true,
    "ero" to true,
    "solo" to true,
    "waifu" to false,
    "pwankg" to true,
    "eron" to true,
    "erokemo" to true
  )

  val endpoints: Set<String> = isNSFW.keys

  data class NekosLifeResponse(
    val url: String = "",
    val isNSFW: Boolean = true
  )

  fun getResponse(type: String): NekosLifeResponse {
    val conn = URL("https://nekos.life/api/v2/img/$type").openConnection()
    conn.setRequestProperty("User-Agent", "Mozilla/5.0")
    val response: NekosLifeResponse = gson.fromJson(conn.getInputStream().bufferedReader())
    return response.copy(isNSFW = isNSFW[type] ?: true)
  }

}
