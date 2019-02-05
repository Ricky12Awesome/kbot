package me.ricky.discord.bot.kbot.command

import javafx.scene.paint.Color
import me.ricky.discord.bot.kbot.handler.CommandEvent
import me.ricky.discord.bot.kbot.handler.RunAt
import me.ricky.discord.bot.kbot.handler.RunAtType
import me.ricky.discord.bot.kbot.handler.Usage
import me.ricky.discord.bot.kbot.handler.UsageArgument
import me.ricky.discord.bot.kbot.handler.UsageType
import me.ricky.discord.bot.kbot.util.embed
import me.ricky.discord.bot.kbot.util.footer
import me.ricky.discord.bot.kbot.util.inlineField
import me.ricky.discord.bot.kbot.handler.notEnoughArgs
import org.javacord.api.entity.permission.PermissionType

/**
 * @property name Name of the command
 * @property description Description of the command. default = "undefined"
 * @property usage Usage of the command. default = [usage]
 * @property aliases Aliases of the command. default = [emptyList]
 * @property permission Permission of the command. default = [PermissionType.SEND_MESSAGES]
 */
interface Command {
  val name: String
  val description: String get() = "undefined"
  val usage: Usage get() = usage()
  val aliases: List<String> get() = emptyList()
  val permission: PermissionType get() = PermissionType.SEND_MESSAGES

  /**
   * @param event used to call [CommandEvent.onEvent]
   */
  fun call(event: CommandEvent) = event.onEvent()

  /**
   * Runs when a command event is called
   */
  fun CommandEvent.onEvent()

  /**
   * Checks if what the user entered is correct usage
   *
   * @param last last index of argument
   */
  fun canRun(last: Int): Int {
    if (usage.isEmpty()) return 0

    usage.runsAt.forEach {
      val (index, type) = it
      if (type.calc(index, last)) return index
    }

    throw notEnoughArgs
  }

  /**
   * TODO: Document usage
   */
  fun usage(runsAt: List<RunAt> = listOf(), vararg args: UsageArgument) =
    Usage(args.toList(), runsAt)

  /**
   * TODO: Document usage message
   */
  fun usageMessage() = buildString {
    append("`${UsageType.REQUIRED.both}` = Required, ")
    append("`${UsageType.OPTIONAL.both}` = Optional")
  }

  /**
   * TODO: Document required
   */
  fun required(vararg args: String) =
    UsageArgument(args.toList(), UsageType.REQUIRED)

  /**
   * TODO: Document optional
   */
  fun optional(vararg args: String) =
    UsageArgument(args.toList(), UsageType.OPTIONAL)

  /**
   * TODO: Document required
   */
  fun required(vararg args: UsageArgument) =
    UsageArgument(args.map { it.format() },
      UsageType.REQUIRED)

  /**
   * TODO: Document optional
   */
  fun optional(vararg args: UsageArgument) =
    UsageArgument(args.map { it.format() },
      UsageType.OPTIONAL)

  /**
   * TODO: Document run at
   */
  fun runAt(vararg args: List<RunAt>) = args.flatMap { it }

  /**
   * TODO: Document exact
   */
  fun exact(vararg args: Int) = args.map { it to RunAtType.EXACT }

  /**
   * TODO: Document after
   */
  fun after(vararg args: Int) = args.map { it to RunAtType.AFTER }

  /**
   * TODO: Document exact or after
   */
  fun exactOrAfter(vararg args: Int) = args.map { it to RunAtType.EXACT_AFTER }

  /**
   * Gives help about this command
   *
   * @return [embed]
   */
  fun help() = embed(
    title = name,
    color = Color.LIME,
    description = description,
    footer = footer(usageMessage()),
    fields = listOf(
      inlineField("Aliases", "$aliases"),
      inlineField("Usage", "$usage"),
      inlineField("Permission", "$permission")
    )
  )
}