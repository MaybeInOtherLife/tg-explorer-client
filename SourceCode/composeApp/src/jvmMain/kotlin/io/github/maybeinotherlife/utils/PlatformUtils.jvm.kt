package io.github.maybeinotherlife.utils

import androidx.compose.runtime.Composable
import java.awt.Desktop
import java.io.File
actual fun getDownloadDirectory(): File {
    val home = System.getProperty("user.home")
    val downloads = File(home, "Downloads")
    if (!downloads.exists()) downloads.mkdirs()
    return downloads
}

actual fun openFile(file: File) {
    runCatching {
        Desktop.getDesktop().open(file)
    }
}

actual fun shareFile(file: File) {
}


actual fun scanMediaFile(filePath: String) {
}



actual fun getDataStorePath(): String {
    return System.getProperty("user.home") + "/.tg_scrapper/preferences.preferences_pb"
}

@Composable
actual fun BackHandler(enabled:Boolean,handle: () -> Unit) {
}