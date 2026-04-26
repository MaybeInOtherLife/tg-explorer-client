package io.github.maybeinotherlife.utils

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

expect fun getDownloadDirectory(): File
expect fun openFile(file: File)

expect fun shareFile(file: File)
expect fun scanMediaFile(filePath: String)


expect fun getDataStorePath(): String

fun createDataStore(path: String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { path.toPath() }
    )
}

@Composable
expect fun BackHandler(enabled:Boolean,handle:()->Unit)
