package dev.ruffrick.jda.commands.event

import dev.ruffrick.jda.commands.CommandRegistry
import dev.ruffrick.jda.kotlinx.event.SuspendEventListener
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import kotlin.reflect.full.callSuspend
import kotlin.system.measureTimeMillis

internal class ButtonClickListener(
    private val commandRegistry: CommandRegistry
) : SuspendEventListener() {

    private val start = System.currentTimeMillis()

    override suspend fun onEvent(event: GenericEvent) {
        if (event !is ButtonClickEvent) return

        if ((event.messageIdLong shr 22) + 1420070400000 < start) return

        val (commandName, buttonId, userId) = event.componentId.split('.').takeIf { it.size == 3 }
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val command = commandRegistry.commandsByName[commandName]
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val userIdLong = userId.toLongOrNull()
            ?: throw IllegalArgumentException("Invalid button: id='${event.componentId}'")

        val (function, private) = commandRegistry.buttonsByKey["$commandName.$buttonId"]
            ?: throw IllegalArgumentException("No button mapping found: id='${event.componentId}'")

        if (private && event.user.idLong != userIdLong) {
            return event.replyEmbeds(
                EmbedBuilder().setDescription("This can only be used by <@$userId>!").build()
            ).setEphemeral(true).queue()
        }

        val duration = measureTimeMillis {
            function.callSuspend(command, event, userIdLong)
        }
        log.debug(
            "Button executed: " +
                    "id='${event.componentId}', " +
                    "userId='${event.user.id}', " +
                    "guildId='${event.guild?.id ?: -1}', " +
                    "durationMs='$duration'"
        )
    }

}