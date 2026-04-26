package io.github.maybeinotherlife

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.maybeinotherlife.components.RootComponent
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun main() = application {

    val rootComponent = RootComponent(
        componentContext = DefaultComponentContext(
            lifecycle = LifecycleRegistry()
        )
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "TGScrapperClient",
    ) {
        App(
            rootComponent = rootComponent
        )
    }
}