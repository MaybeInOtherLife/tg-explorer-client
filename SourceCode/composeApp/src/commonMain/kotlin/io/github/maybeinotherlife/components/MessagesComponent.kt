package io.github.maybeinotherlife.components

import com.arkivanov.decompose.ComponentContext
import io.github.maybeinotherlife.models.ApiResponse
import io.github.maybeinotherlife.models.Channel
import io.github.maybeinotherlife.models.Message
import io.github.maybeinotherlife.network.ApiClient.httpClient
import io.github.maybeinotherlife.network.EndPoints
import io.github.maybeinotherlife.utils.deserialize
import io.github.maybeinotherlife.utils.getDownloadDirectory
import io.github.maybeinotherlife.utils.scanMediaFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.File

class MessagesComponent(
    componentContext: ComponentContext,
    val onBack: () -> Unit
) : ComponentContext by componentContext {

    data class DownloadState(
        val messageId: Int,
        val status: DownloadStatus,
        val progress: Float = 0f,
        val filePath: String? = null
    )

    sealed interface DownloadStatus {
        data object Downloading : DownloadStatus
        data object Downloaded : DownloadStatus
    }

    data class State(
        val messages: ApiResponse<List<Message>> = ApiResponse.Loading,
        val refreshing: Boolean = false,
        val downloads: Map<Int, DownloadState> = emptyMap()
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var channel: String? = null

    fun getMessages(channel: String?, refresh: Boolean) {
        channel?.let { this.channel = channel }
        componentScope.launch {
            if (refresh) {
                _state.update { it.copy(refreshing = true) }
            } else {
                _state.update { it.copy(messages = ApiResponse.Loading) }
            }
            runCatching {
                val response = withContext(Dispatchers.IO) {
                    httpClient
                        .newCall(
                            Request.Builder()
                                .get()
                                .url(EndPoints.messages(this@MessagesComponent.channel!!))
                                .build()
                        ).execute().body.string()
                }
                response.deserialize<List<Message>>()
            }
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            messages = ApiResponse.Success(data = result),
                            refreshing = false
                        )
                    }
                }
                .onFailure { err ->
                    err.printStackTrace()
                    _state.update {
                        it.copy(
                            messages = ApiResponse.Error(error = err.message.orEmpty()),
                            refreshing = false
                        )
                    }
                }
        }
    }

    fun downloadMessage(message: Message) {
        componentScope.launch {
            val id = message.id
            val url = EndPoints.media(channel = channel!!, media = message.media!!)

            _state.update {
                it.copy(
                    downloads = it.downloads + (
                            id to DownloadState(
                                messageId = id,
                                status = DownloadStatus.Downloading,
                                progress = 0f
                            )
                            )
                )
            }

            runCatching {
                withContext(Dispatchers.IO) {
                    val dir = getDownloadDirectory().resolve("tg_scrapper")
                    dir.mkdirs()
                    val file = File(dir, url.substringAfterLast("/"))
                    val request = Request.Builder().url(url).build()
                    httpClient.newCall(request).execute().use { response ->
                        val body = response.body

                        val total = body.contentLength()
                        var readTotal = 0L

                        body.byteStream().use { input ->
                            file.outputStream().use { output ->
                                val buffer = ByteArray(8192)
                                while (true) {
                                    val read = input.read(buffer)
                                    if (read == -1) break

                                    output.write(buffer, 0, read)
                                    readTotal += read

                                    if (total > 0) {
                                        val progress = readTotal.toFloat() / total
                                        _state.update { state ->
                                            val d = state.downloads[id] ?: return@update state
                                            state.copy(
                                                downloads = state.downloads + (id to d.copy(progress = progress))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    file.absolutePath
                }
            }.onSuccess { path ->
                _state.update { state ->
                    val d = state.downloads[id] ?: return@update state
                    state.copy(
                        downloads = state.downloads + (
                                id to d.copy(
                                    status = DownloadStatus.Downloaded,
                                    progress = 1f,
                                    filePath = path
                                ))
                    )
                }
                scanMediaFile(path)
            }.onFailure {
                it.printStackTrace()
                _state.update { state ->
                    state.copy(
                        downloads = state.downloads - id
                    )
                }
            }
        }
    }
    fun isMessageDownloaded(message: Message): Boolean {
        return try {
            val downloadDir = getDownloadDirectory().resolve("tg_scrapper")
            val fileName = message.media.orEmpty()
            val file = File(downloadDir, fileName)
            file.exists()
        } catch (e: Exception) {
            false
        }
    }

}
