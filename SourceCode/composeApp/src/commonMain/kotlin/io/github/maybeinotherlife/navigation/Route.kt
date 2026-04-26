package io.github.maybeinotherlife.navigation

import io.github.maybeinotherlife.models.Channel
import kotlinx.serialization.Serializable


@Serializable
sealed class Route {

    @Serializable
    data object ChannelList: Route()


    @Serializable
    data class Messages(val channel: Channel):Route()
}