package me.ricky.discord.bot.kbot.command

import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.util.info
import me.ricky.discord.bot.kbot.util.send
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.Role
import java.util.concurrent.CompletableFuture

class RoleInfoCommand : Command {
  override val name: String = "roleinfo"
  override val description: String = "Gives info about a role"
  override val aliases: List<String> = listOf("ri")
  override val permission: PermissionType = PermissionType.ADMINISTRATOR
  override val usage: Usage = usage()

  override fun CommandEvent.onEvent() {
    val str: String? = if (args.lastIndex >= 1)
      args.slice(1..args.lastIndex).joinToString(" ") else null
    val roles = roles(str)
    val send: (List<Role>) -> CompletableFuture<Message> = { r ->
      channel.send("Roles: ${r.joinToString(", ", "```json\n[\n", "\n]\n```") { "\"${it.name}\"" }}")
    }

    when (roles.size) {
      0 -> channel.send("No Roles where found with `${args[1]}`")
      1 -> channel.send(roles[0].info())
      else -> send(roles)
    }
  }

  private fun CommandEvent.roles(name: String? = null): List<Role> =
    if (name == null) server.roles else server.roles.filter { it.name.toLowerCase().contains(name.toLowerCase()) }
}