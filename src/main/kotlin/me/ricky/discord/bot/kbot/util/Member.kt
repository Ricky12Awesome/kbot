package me.ricky.discord.bot.kbot.util

import javafx.scene.paint.Color
import org.javacord.api.entity.permission.Role
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

interface Member : User {
  val server: Server
  val roles: List<Role>
  val roleColor: Color
}

data class MemberData(
  private val delegate: User,
  override val server: Server,
  override val roles: List<Role>,
  override val roleColor: Color
) : Member, User by delegate