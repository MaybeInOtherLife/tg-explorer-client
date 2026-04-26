package io.github.maybeinotherlife

import android.graphics.Color.TRANSPARENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.github.maybeinotherlife.components.RootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val systemBarStyle  = SystemBarStyle.dark(TRANSPARENT)
        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle
        )
        super.onCreate(savedInstanceState)
        val rootComponent = RootComponent(
            componentContext = defaultComponentContext()
        )
        TGScrapperClientApp.context = this
        setContent {
            App(
                rootComponent = rootComponent
            )
        }
    }
}

