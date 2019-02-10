package me.ricky.discord.bot.kbot.command.management

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandCategory
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.ReplayHandler
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.handler.handleException
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.util.rolesContainingName
import me.ricky.discord.bot.kbot.util.send
import me.ricky.discord.bot.kbot.util.database.sqlUpdate
import me.ricky.discord.bot.kbot.util.stringList
import me.ricky.discord.bot.kbot.util.toMember
import me.ricky.discord.bot.kbot.util.value
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Role
import java.util.concurrent.TimeUnit

class SettingsCommand(val handler: ReplayHandler) : Command {
  override val name: String = "settings"
  override val description: String = "Change the settings of the bot for this server"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val category: CommandCategory = CommandCategory.MANAGEMENT
  private val options = arrayOf(
    "prefix",
    "xp-scale",
    "log-channel",
    "mute-role",
    "currency-name",
    "command-channel",
    "currency-symbol"
  )

  override val usage: Usage = usage(
    runAt(exact(0), exactOrAfter(2)),
    required(*options),
    optional("new setting")
  )

  override fun CommandEvent.onEvent() {
    when {
      runAt == 0 -> {
        channel.send(
          title = "Current Settings",
          color = server.owner.toMember(server).roleColor,
          fields = listOf(
            inlineField("Prefix", server.data.prefix),
            inlineField("Log Channel", "${server.logChannel?.mentionTag}"),
            inlineField("Command Channel", "${server.commandChannel?.mentionTag}"),
            inlineField("Mute Role", "${server.muteRole?.mentionTag}"),
            inlineField("XP Scale", "${server.data.xpScalar}"),
            inlineField("Currency Name", server.data.currencyName),
            inlineField("Currency Symbol", server.data.currencySymbol)
          )
        )
      }
      runAt >= 2 -> run(args[1], args.slice(2..args.lastIndex).joinToString(" "))
    }
  }

  fun CommandEvent.channelCheck(value: String, name: String = ""): ServerTextChannel {
    val id = value.replace(Regex("[<#>]"), "").toLongOrNull()

    return server.getTextChannelById(id ?: 0).value ?: throw exception("invalid $name channel")
  }

  fun CommandEvent.roleCheck(value: String, callback: (Role) -> Unit) {
    val roles = server.rolesContainingName(value)

    if (roles.size == 1) {
      callback(roles[0])
      return
    }

    channel.send(
      title = "Select A Role",
      description = stringList(
        *roles.mapIndexed { index, role -> "**$index**: ${role.name}" }.toTypedArray(),
        "You have 5 seconds."
      )
    )

    handler.register<Role, Unit>(
      id = member.id,
      time = 5L to TimeUnit.SECONDS,
      callback = { callback(it) },
      handle = { handleException(it) },
      get = {
        val index = messageContent.toIntOrNull() ?: throw exception("Invalid Number")
        roles.getOrNull(index) ?: throw exception("That doesn't exist.")
      }
    )
  }

  fun CommandEvent.run(selected: String, value: String): Any = when (selected) {
    "prefix" -> {
      val old = server.data.prefix
      val new = if (value.length < 3) value else throw exception("prefix is too long, max 2 chars")
      server.sqlUpdate { it[prefix] = new }
      channel.send("Changed prefix from $old to $new")
    }
    "xp-scale" -> {
      val old = server.data.xpScalar
      val new = value.toDoubleOrNull() ?: throw exception("xp-scale must be a number.")
      server.sqlUpdate { it[xpScalar] = new }
      channel.send("Changed xp-scale from $old to $new")
    }
    "log-channel" -> {
      val old = server.logChannel
      val new = channelCheck(value, "log")
      server.sqlUpdate { it[logChannelId] = new.id }
      channel.send("Changed log channel from ${old?.mentionTag} to ${new.mentionTag}")
    }
    "command-channel" -> {
      val old = server.commandChannel
      val new = channelCheck(value, "command")
      server.sqlUpdate { it[commandChannelId] = new.id }
      channel.send("Changed command channel from ${old?.mentionTag} to ${new.mentionTag}")
    }
    "mute-role" -> roleCheck(value) {
      val old = server.muteRole
      server.sqlUpdate { update -> update[muteRoleId] = it.id }
      channel.send("Changed mute role from ${old?.mentionTag} to ${it.mentionTag}")
    }
    "currency-name" -> {
      val old = server.data.currencyName
      server.sqlUpdate { it[currencyName] = value }
      channel.send("Changed currency name from $old to $value")
    }
    "currency-symbol" -> {
      val old = server.data.currencySymbol
      val new = if (value.length == 1) value else throw exception("currency-symbol can only contain 1 char.")
      server.sqlUpdate { it[currencySymbol] = new }
      channel.send("Changed currency symbol from $old to $new")
    }
    else -> throw exception("Invalid Option")
  }

}