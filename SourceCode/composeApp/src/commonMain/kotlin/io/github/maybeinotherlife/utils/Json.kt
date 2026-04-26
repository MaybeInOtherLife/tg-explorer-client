package io.github.maybeinotherlife.utils

import io.github.maybeinotherlife.network.ApiClient.json
import kotlinx.serialization.json.Json

inline fun <reified T> String.deserialize(): T {
    return json.decodeFromString(this)
}