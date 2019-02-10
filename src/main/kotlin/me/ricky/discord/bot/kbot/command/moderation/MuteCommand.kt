package me.ricky.discord.bot.kbot.command.moderation

import me.ricky.discord.bot.kbot.command.Command
import me.ricky.discord.bot.kbot.handler.CommandCategory
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.PunishmentHandler
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.exception
import me.ricky.discord.bot.kbot.util.convertOrNull
import me.ricky.discord.bot.kbot.util.formattedTime
import me.ricky.discord.bot.kbot.util.getSQLMember
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.permission.PermissionType

class MuteCommand(val handler: PunishmentHandler) : Command {
  override val name: String = "mute"
  override val description: String = "Mutes users"
  override val aliases: List<String> = listOf()
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val category: CommandCategory = CommandCategory.MODERATION
  override val usage: Usage = usage(
    runAt(exactOrAfter(3)),
    required("user"),
    required("time"),
    required("reason...")
  )

  override fun CommandEvent.onEvent() {
    val id = args[1].replace(Regex("[@!<>]"), "").toLongOrNull() ?: throw exception("Invalid Member")
    val time = convertOrNull(args[2]) ?: throw exception("Invalid Time")
    val reason = args.slice(3..args.lastIndex).joinToString(" ")
    val to = server.getSQLMember(id) ?: throw exception("Can't find Member.")

    if (to.roles.firstOrNull { it.allowedPermissions.contains(PermissionType.ADMINISTRATOR) } != null) {
      channel.send(":no_entry_sign: Sorry, you can't mute admins.")
      return
    }

    handler.mute(member, to, time, reason)
    channel.send(":white_check_mark: Muted ${to.discriminatedName} for `${formattedTime(time)}` reason `$reason`")
  }
}