package io.github.maybeinotherlife.network

object EndPoints {
    const val BASE_URL = "https://raw.githubusercontent.com/MaybeInOtherLife/tg-scrapper/refs/heads/master/data/"

    val CHANNEL_LIST: String get() = "index.json?v=${System.currentTimeMillis()}"

    fun channelProfile(channel: String,name:String): String {
        return media(channel,name)
    }

    fun messages(channel: String):String{
        return "${BASE_URL}channels/$channel/messages.json?v=${System.currentTimeMillis()}"
    }
    fun media(channel:String,media:String):String{
        return "${BASE_URL}channels/$channel/$media"
    }
}