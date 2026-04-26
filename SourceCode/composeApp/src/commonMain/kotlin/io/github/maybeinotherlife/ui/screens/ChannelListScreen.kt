package io.github.maybeinotherlife.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.github.maybeinotherlife.Constants
import io.github.maybeinotherlife.components.ChannelListComponent
import io.github.maybeinotherlife.components.datastore
import io.github.maybeinotherlife.components.getDatastoreKeyForChannel
import io.github.maybeinotherlife.models.ApiResponse
import io.github.maybeinotherlife.models.MessageType
import io.github.maybeinotherlife.network.EndPoints
import io.github.maybeinotherlife.ui.components.ResponseContent
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.stringResource
import tgscrapperclient.composeapp.generated.resources.Res

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListScreen(component: ChannelListComponent) {
    val state by component.state.collectAsState()
    val context = LocalPlatformContext.current
    val uriHandler = LocalUriHandler.current

    var showInfoDialog by remember { mutableStateOf(false) }
    if(showInfoDialog){
        Dialog(
            onDismissRequest = {
                showInfoDialog = false
            },
            content = {
                Card {
                    Column(modifier=Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TG Scrapper Client", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        Text(text = "A way to explore telegram without sanctions", fontSize = 14.sp, textAlign = TextAlign.Center)
                        Row {
                            Button(
                                onClick = {
                                    uriHandler.openUri(Constants.GH_PAGE)
                                }
                            ){
                                Text("Star on github")
                            }
                            Button(
                                onClick = {
                                    uriHandler.openUri(Constants.LATEST_RELEASE)
                                }
                            ){
                                Text("Update")
                            }
                        }
                    }
                }
            }
        )
    }
    Column {
        MediumTopAppBar(
            title = {
                Row(modifier=Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "Channel List"
                        )
                        AnimatedVisibility(
                            visible = state.channelList is ApiResponse.Success
                        ){
                            val lastUpdate = remember(state.channelList) {
                                (state.channelList as? ApiResponse.Success)?.data?.firstOrNull()?.lastUpdated?.persianFormat()
                            }
                            Text(
                                text = "Update: $lastUpdate",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            showInfoDialog = true
                        }
                    ){
                        Icon(
                            imageVector = Icons.Default.Info,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        AnimatedVisibility(
            visible = state.refreshing
        ){
            Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center){
                Text("Refreshing...")
            }
        }
        PullToRefreshBox(
            isRefreshing = false,
            onRefresh = {
                component.getChannels(refresh = true)
            },
            content = {
                ResponseContent(
                    response = state.channelList,
                    successContent = {
                        val channels = it.data
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(
                                start = 22.dp,
                                end = 22.dp,
                                top = 22.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            ),
                        ) {
                            items(channels, key = { it.username }) { channel ->
                                val lastReadMessageId by datastore.data.map {
                                    it[getDatastoreKeyForChannel(channel)]
                                }.collectAsState(initial = null)
                                val unreadCount = lastReadMessageId?.let {
                                    ((channel.lastMessage?.id ?: 0) - it).coerceAtLeast(0)
                                } ?: 0
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        component.onChannelClick(channel)
                                    },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceTint
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                        ) {
                                            channel.profilePhoto?.let { profile ->
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .crossfade(400)
                                                        .data(
                                                            EndPoints.channelProfile(
                                                                channel = channel.username,
                                                                name = profile
                                                            )
                                                        )
                                                        .build(),
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier=Modifier.weight(1f)) {
                                            Text(
                                                text = channel.title,
                                                maxLines = 1,
                                                fontSize = 14.sp,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if(channel.lastMessage != null){
                                                val lastMessagePreview = when(channel.lastMessage.type){
                                                    MessageType.Text -> channel.lastMessage.text.orEmpty()
                                                    else-> channel.lastMessage.caption  ?: channel.lastMessage.type.toString()
                                                }
                                                Text(
                                                    text = lastMessagePreview.replace("*",""),
                                                    fontSize = 13.sp,
                                                    overflow = TextOverflow.Ellipsis,
                                                    maxLines = 1,
                                                )
                                            }
                                        }
                                        if(unreadCount > 0){
                                            Box(
                                                modifier = Modifier.padding(start = 12.dp)
                                                    .height(20.dp)
                                                    .widthIn(min = 20.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 4.dp)
                                                ,
                                                contentAlignment = Alignment.Center
                                            ){
                                                Text(
                                                    text = unreadCount.toString(),
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.offset(y = -2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    doOnRetry = {
                        component.getChannels(false)
                    }
                )
            }
        )
    }
}