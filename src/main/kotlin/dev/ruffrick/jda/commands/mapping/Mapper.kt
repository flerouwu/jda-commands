package dev.ruffrick.jda.commands.mapping

import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.Channel
import net.dv8tion.jda.api.entities.channel.concrete.Category
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions

interface Mapper<S, T> {

    suspend fun transform(value: S): T

    val input: KClass<*>
        get() = this::class.memberFunctions.first { it.name == "transform" }.parameters[1].type.classifier as KClass<*>

    val output: KClass<*>
        get() = this::class.memberFunctions.first { it.name == "transform" }.returnType.classifier as KClass<*>

    val type: OptionType
        get() = when (input) {
            String::class -> OptionType.STRING
            Long::class -> OptionType.INTEGER
            Boolean::class -> OptionType.BOOLEAN
            User::class -> OptionType.USER

            // Channels
            Channel::class -> OptionType.CHANNEL
            TextChannel::class -> OptionType.CHANNEL
            VoiceChannel::class -> OptionType.CHANNEL
            Category::class -> OptionType.CHANNEL
            NewsChannel::class -> OptionType.CHANNEL
            ThreadChannel::class -> OptionType.CHANNEL
            StageChannel::class -> OptionType.CHANNEL
            ForumChannel::class -> OptionType.CHANNEL

            Role::class -> OptionType.ROLE
            IMentionable::class -> OptionType.MENTIONABLE
            Double::class -> OptionType.NUMBER
            else -> throw IllegalArgumentException("Invalid input type: ${input.qualifiedName}")
        }
}
