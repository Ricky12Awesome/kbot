package me.ricky.discord.bot.kbot.util

import me.ricky.discord.bot.kbot.util.database.SQLMember
import me.ricky.discord.bot.kbot.util.database.sqlUpdate

data class XPLevelHandler(val scalar: Double) {
  fun xpToLevel(exp: Double): Long = Math.sqrt(exp / scalar).toLong()
  fun levelToXP(level: Long): Double = Math.pow(level.toDouble(), 2.0) * scalar
  fun xpForNextLevel(exp: Double) = levelToXP(xpToLevel(exp) + 1) - exp

  fun addXP(member: SQLMember, amount: Double) = member.sqlUpdate { it[xp] = member.data.xp + amount }
  fun setXP(member: SQLMember, amount: Double) = member.sqlUpdate { it[xp] = amount }
}
