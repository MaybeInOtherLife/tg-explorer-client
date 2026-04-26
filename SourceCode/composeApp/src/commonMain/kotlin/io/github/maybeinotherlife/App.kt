package io.github.maybeinotherlife

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.github.maybeinotherlife.components.RootComponent
import io.github.maybeinotherlife.ui.RootContent

@Composable
@Preview
fun App(rootComponent: RootComponent) {
    val DarkColorScheme = darkColorScheme(
        primary = Color(0xFF367EFF),
        onPrimary = Color(0xFF001A41),
        primaryContainer = Color(0xFF0F2F66),
        onPrimaryContainer = Color(0xFFD6E3FF),

        secondary = Color(0xFFBDC7DC),
        onSecondary = Color(0xFF273041),
        secondaryContainer = Color(0xFF3D4758),
        onSecondaryContainer = Color(0xFFD9E2F9),

        tertiary = Color(0xFFDDBCE0),
        onTertiary = Color(0xFF402843),
        tertiaryContainer = Color(0xFF583E5B),
        onTertiaryContainer = Color(0xFFFAD8FD),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),

        background = Color(0xFF111111),
        onBackground = Color(0xFFE4E4E6),

        surface = Color(0xFF111111),
        onSurface = Color(0xFFE4E4E6),

        surfaceVariant = Color(0xFF44474F),
        onSurfaceVariant = Color(0xFFC4C6CF),

        outline = Color(0xFF8E9099),
        outlineVariant = Color(0xFF44474F),

        inverseSurface = Color(0xFFE4E4E6),
        inverseOnSurface = Color(0xFF1A1B1E),
        inversePrimary = Color(0xFF2F5DAA),

        surfaceTint = Color(0xff202020),
        scrim = Color(0xFF111111)
    )
    MaterialTheme(
        colorScheme = DarkColorScheme
    ) {
        Surface {
            RootContent(
                component = rootComponent
            )
        }
    }
}