package dev.ruffrick.jda.commands.annotations

import net.dv8tion.jda.api.entities.channel.ChannelType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ChannelTypes(vararg val channelTypes: ChannelType)
