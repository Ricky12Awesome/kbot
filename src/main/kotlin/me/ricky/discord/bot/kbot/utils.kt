package me.ricky.discord.bot.kbot

import javafx.scene.paint.Color

fun Color.convert(): java.awt.Color = java.awt.Color.getHSBColor(hue.toFloat(), saturation.toFloat(), brightness.toFloat())