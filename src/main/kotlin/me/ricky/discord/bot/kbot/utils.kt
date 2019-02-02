package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color
import org.javacord.api.entity.user.User

val User.avatarUrl get() = avatar.url.toString()

fun stringList(vararg string: String, separator: String = "") = string.joinToString("$separator\n")

fun Color.convert(): java.awt.Color = java.awt.Color(
  red.toFloat(),
  green.toFloat(),
  blue.toFloat(),
  opacity.toFloat()
)