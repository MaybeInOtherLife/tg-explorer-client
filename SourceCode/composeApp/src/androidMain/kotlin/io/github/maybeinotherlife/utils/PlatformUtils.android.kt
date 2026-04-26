package io.github.maybeinotherlife.utils

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import io.github.maybeinotherlife.TGScrapperClientApp
import java.io.File

actual fun getDownloadDirectory(): File {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
}

actual fun openFile(file: File) {
    val context = TGScrapperClientApp.context
    val mimeType = MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(file.extension.lowercase())
        ?: "*/*"


    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    runCatching {
        context.startActivity(intent)
        true
    }
        .onFailure {
            it.printStackTrace()
            (it.localizedMessage ?: it::class.qualifiedName)?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        .getOrElse { false }
}

actual fun shareFile(file: File) {
    val context = TGScrapperClientApp.context

    val mimeType = MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(file.extension.lowercase())
        ?: "*/*"
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share file"))
}

actual fun scanMediaFile(filePath: String) {
    MediaScannerConnection.scanFile(
        TGScrapperClientApp.context,
        arrayOf(filePath),
        null
    ) { path, uri ->
    }
}


actual fun getDataStorePath(): String {
    return TGScrapperClientApp.context.filesDir.resolve("preferences.preferences_pb").absolutePath
}

@Composable
actual fun BackHandler(enabled:Boolean,handle: () -> Unit) {
    BackHandler(enabled) { handle() }
}