package io.github.maybeinotherlife.components

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.arkivanov.decompose.ComponentContext
import io.github.maybeinotherlife.models.ApiResponse
import io.github.maybeinotherlife.models.Channel
import io.github.maybeinotherlife.models.Message
import io.github.maybeinotherlife.network.ApiClient.httpClient
import io.github.maybeinotherlife.network.EndPoints
import io.github.maybeinotherlife.utils.createDataStore
import io.github.maybeinotherlife.utils.deserialize
import io.github.maybeinotherlife.utils.getDataStorePath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import kotlin.time.Duration.Companion.seconds


fun getDatastoreKeyForChannel(channel: Channel): Preferences.Key<Int> {
    return intPreferencesKey("last_read_${channel.username}")
}
val datastore = createDataStore(path = getDataStorePath())

class ChannelListComponent(
    componentContext: ComponentContext,
    val onChannelClick:(Channel)->Unit,
): ComponentContext by componentContext{
    data class State(
        val channelList: ApiResponse<List<Channel>> = ApiResponse.Loading,
        val refreshing: Boolean = false
    )
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()


    init {
        getChannels(
            refresh = false
        )

        componentScope.launch {
            while(isActive){
                delay(10.seconds)
                getChannels(refresh = true)
            }
        }
    }
    fun getChannels(refresh: Boolean){
        componentScope.launch {
            if(refresh){
                _state.update { it.copy(refreshing = true) }
            }else{
                _state.update { it.copy(channelList = ApiResponse.Loading) }
            }
            runCatching {
                val response = withContext(Dispatchers.IO){
                    httpClient
                        .newCall(
                            Request.Builder()
                                .get()
                                .url("${EndPoints.BASE_URL}${EndPoints.CHANNEL_LIST}")
                                .build()
                        ).execute().body.string()
                }
                response.deserialize<List<Channel>>()
            }
                .onSuccess { result->
                    // each on channels, if last read message id is null, set last message id as read
                    result.forEach { channel->
                        componentScope.launch {
                            val key = getDatastoreKeyForChannel(channel = channel)
                            val lastReadMessageId = datastore.data.map { it[key] }.first()
                            if(lastReadMessageId == null){
                                channel.lastMessage?.id?.let { lastMessageId->
                                    datastore.edit {
                                        it[getDatastoreKeyForChannel(channel = channel)] = lastMessageId
                                    }
                                }
                            }
                        }
                    }
                    _state.update { it.copy(channelList = ApiResponse.Success(data = result), refreshing = false) }
                }
                .onFailure { err->
                    err.printStackTrace()
                    _state.update { it.copy(channelList = ApiResponse.Error(error = err.message.orEmpty()), refreshing = false) }
                }
        }
    }
}