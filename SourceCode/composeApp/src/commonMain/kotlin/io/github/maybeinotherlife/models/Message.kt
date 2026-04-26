package io.github.maybeinotherlife.models

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class Message(
    val id: Int,
    val date: Instant,
    val media: String?,
    val caption:String?,
    val text: String?,
){
    val type : MessageType get() = when{
        media != null -> {
            if(media.endsWith(".jpg") || media.endsWith(".png") || media.endsWith(".webp")){
                MessageType.Photo
            }else if(media.endsWith(".mp4") || media.endsWith(".mov") || media.endsWith(".mkv")){
                MessageType.Video
            }else if(media.endsWith(".mp3") || media.endsWith(".ogg") || media.endsWith(".mav") || media.endsWith(".m4a")){
                MessageType.Audio
            }else{
                MessageType.Document
            }
        }
        else-> MessageType.Text
    }
}

enum class MessageType{
    Text,Photo,Audio,Video,Document
}
