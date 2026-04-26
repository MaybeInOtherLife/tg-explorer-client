package io.github.maybeinotherlife.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class Channel(
    val name:String,
    val title:String,
    val username:String,
    val lastUpdated: Instant,
    val participantsCount:Int,
    val profilePhoto:String?,
    val lastMessage: Message?
)