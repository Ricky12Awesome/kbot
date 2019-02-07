package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.sqlUpdate
import me.ricky.discord.bot.kbot.util.toMember
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.entity.permission.PermissionType

class SettingsCommand : Command {
  override val name: String = "settings"
  override val description: String = "Change the settings of the bot for this server"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  private val options = arrayOf(
    "prefix",
    "xp-scale",
    "log-channel",
    "currency-name",
    "command-channel",
    "currency-symbol"
  )

  override val usage: Usage = usage(
    exact(0, 2),
    required(*options),
    optional("new setting")
  )

  override fun CommandEvent.onEvent() {
    when (runAt) {
      0 -> {
        channel.send(
          title = "Current Settings",
          color = server.owner.toMember(server).roleColor,
          fields = listOf(
            inlineField("Prefix", server.data.prefix),
            inlineField("Log Channel", "${server.logChannel?.mentionTag}"),
            inlineField("Command Channel", "${server.commandChannel?.mentionTag}"),
            inlineField("XP Scale", "${server.data.xpScalar}"),
            inlineField("Currency Name", server.data.currencyName),
            inlineField("Currency Symbol", server.data.currencySymbol)
          )
        )
      }
      2 -> run(args[1], args[2]).join()
    }
  }

  fun CommandEvent.channelCheck(value: String, name: String = ""): Long {
    val id = value.replace(Regex("[<#>]"), "").toLongOrNull()

    return server.getChannelById(id ?: 0).value?.id ?: throw exception("invalid $name channel")
  }

  fun CommandEvent.run(selected: String, value: String) = server.sqlUpdate { update ->
    val msg: String = when (selected) {
      "prefix" -> {
        val old = server.data.prefix
        val new = if (value.length < 3) value else throw exception("prefix is too long, max 2 chars")
        update[prefix] = new
        "Changed prefix from $old to $new"
      }
      "xp-scale" -> {
        val old = server.data.xpScalar
        val new = value.toDoubleOrNull() ?: throw exception("xp-scale must be a number.")
        update[xpScalar] = new
        "Changed xp-scale from $old to $new"
      }
      "log-channel" -> {
        val old = server.data.logChannelId
        val new = channelCheck(value, "log")
        update[logChannelId] = new
        "Changed log channel from $old to $new"
      }
      "command-channel" -> {
        val old = server.data.commandChannelId
        val new = channelCheck(value, "command")
        update[commandChannelId] = new
        "Changed command channel from $old to $new"
      }
      "currency-name" -> {
        val old = server.data.currencyName
        update[currencyName] = value
        "Changed currency name from $old to $value"
      }
      "currency-symbol" -> {
        val old = server.data.currencySymbol
        val new = if (value.length == 1) value else throw exception("currency-symbol can only contain 1 char.")
        update[currencySymbol] = new
        "Changed currency symbol from $old to $new"
      }
      else -> throw exception("Invalid Option")
    }
    channel.send(msg)
  }
}