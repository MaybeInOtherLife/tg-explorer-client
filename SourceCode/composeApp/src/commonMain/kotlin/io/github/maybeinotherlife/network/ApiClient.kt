package io.github.maybeinotherlife.network

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

object ApiClient {
    val httpClient = OkHttpClient.Builder()
//        .connectionSpecs(listOf(
//            ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
//                .build()
//        ))
        .build()
    val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
        coerceInputValues = true
    }
}