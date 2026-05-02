package io.github.maybeinotherlife.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState
import io.github.maybeinotherlife.components.MessagesComponent
import io.github.maybeinotherlife.models.Channel
import io.github.maybeinotherlife.models.Message
import io.github.maybeinotherlife.models.MessageType
import io.github.maybeinotherlife.network.EndPoints
import io.github.maybeinotherlife.ui.components.ResponseContent
import io.github.maybeinotherlife.utils.BackHandler
import io.github.maybeinotherlife.utils.getDownloadDirectory
import io.github.maybeinotherlife.utils.openFile
import io.github.maybeinotherlife.utils.shareFile
import ir.amirroid.jalalidate.format
import ir.amirroid.jalalidate.toJalaliDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MessagesScreen(
    channel: Channel,
    component: MessagesComponent
) {
    val state by component.state.collectAsState()
    LaunchedEffect(Unit) {
        component.getMessages(channel = channel.username, refresh = false)
    }
    val context = LocalPlatformContext.current


    var fullScreenImage by remember { mutableStateOf<String?>(null) }
    BackHandler(fullScreenImage != null) {
        fullScreenImage = null
    }
    FullScreenImage(fullScreenImage)

    BoxWithConstraints {
        val width = maxWidth
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .statusBarsPadding()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = component.onBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Box(
                    Modifier.size(50.dp).clip(CircleShape)
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
                Column {
                    Text(
                        text = channel.title,
                        maxLines = 1,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Row {
                        Text(
                            text = "${channel.participantsCount} Members",
                            fontSize = 12.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.White
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "Update: ${
                                remember {
                                    channel.lastUpdated.persianFormat()
                                }
                            }",
                            fontSize = 11.sp,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            color = Color.White.copy(.5f)
                        )
                    }
                }
            }
            ResponseContent(
                response = state.messages,
                successContent = {
                    val messages = remember(it.data) {
                        it.data.sortedByDescending { it.id }
                            .filter {
                                // ignore messages which they have media but without extension
                                when {
                                    it.media == null-> true
                                    else-> it.media.split(".").lastOrNull() != null
                                }
                            }
                    }
                    LazyColumn(
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding(),
                        ),
                    ) {
                        items(messages, key = { it.id }) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                val cp = LocalClipboardManager.current
                                Column(
                                    modifier = Modifier.widthIn(max = (width.value * .85).dp)
                                        .width(IntrinsicSize.Max)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 0.dp,
                                                topEnd = 16.dp,
                                                bottomStart = 16.dp,
                                                bottomEnd = 16.dp
                                            )
                                        )
                                        .background(MaterialTheme.colorScheme.surfaceTint)
                                        .combinedClickable(
                                            onLongClick = {
                                                (it.text ?: it.caption)?.let {
                                                    cp.setText(buildAnnotatedString { append(it.replace("*","")) })
                                                }
                                            },
                                            onClick = {
                                                if(it.type == MessageType.Photo){
                                                    fullScreenImage = EndPoints.media(channel = channel.username,media = it.media.orEmpty())
                                                }
                                            }
                                        )
                                        .padding(12.dp)
                                ) {
                                    when (it.type) {
                                        MessageType.Text -> TextMessageContent(content = (it.text ?: it.caption).orEmpty())
                                        else -> MediaMessageContent(
                                            message = it,
                                            channel = channel.username,
                                            component = component,
                                            state = state,
                                        )
                                    }
                                    Text(
                                        text = remember(it.date) {
                                            it.date.persianFormat()
                                        },
                                        fontSize = 11.sp,
                                        color = Color.White.copy(.5f),
                                        modifier = Modifier.offset(y = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                doOnRetry = {
                    component.getMessages(channel = channel.username, refresh = false)
                }
            )
        }
    }
}


@Composable
private fun FullScreenImage(fullScreenImage: String?) {
    var lastImage by remember { mutableStateOf(fullScreenImage) }
    if (fullScreenImage != null) {
        lastImage = fullScreenImage
    }

    AnimatedVisibility(
        visible = fullScreenImage != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.zIndex(2f)
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(.8f))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)

                            val maxX = (size.width * (scale - 1)) / 2f
                            val maxY = (size.height * (scale - 1)) / 2f

                            offsetX = (offsetX + pan.x).coerceIn(-maxX, maxX)
                            offsetY = (offsetY + pan.y).coerceIn(-maxY, maxY)

                            if (scale == 1f) {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
            )
            lastImage?.let {
                AsyncImage(
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offsetX
                            translationY = offsetY
                        },
                    model = it,
                    contentDescription = null
                )
            }
        }
    }
}

fun Instant.persianFormat(): String {
    return toLocalDateTime(TimeZone.currentSystemDefault())
        .toJalaliDateTime()
        .format {
            byUnicodePattern("yyyy/MM/dd HH:mm")
        }
}

@Composable
fun TextMessageContent(content: String) {

    val direction = remember(content) {
        if (content.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }) {
            LayoutDirection.Rtl
        } else {
            LayoutDirection.Ltr
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        val defaultTextStyle = LocalTextStyle.current.copy(
            textAlign = if (direction == LayoutDirection.Rtl) TextAlign.Right else TextAlign.Left
        )
        Markdown(
            markdownState = rememberMarkdownState(
                content = content.replace("*", ""),
            ),
            colors = DefaultMarkdownColors(
                text = Color.White,
                codeBackground = Color.Gray,
                dividerColor = Color.White,
                inlineCodeBackground = Color.Gray,
                tableBackground = Color.Transparent,
            ),
            typography = DefaultMarkdownTypography(
                h1 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h2 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h3 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h4 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h5 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                h6 = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                text = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                ),
                code = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                ),
                inlineCode = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                ),
                quote = defaultTextStyle.copy(
                    fontSize = 13.sp,
                ),
                paragraph = defaultTextStyle.copy(
                    fontSize = 13.sp,
                ),
                ordered = defaultTextStyle.copy(
                    fontSize = 13.sp,
                ),
                bullet = defaultTextStyle.copy(
                    fontSize = 13.sp,
                ),
                list = defaultTextStyle.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                ),
                textLink = TextLinkStyles(
                    style = defaultTextStyle.copy(
                        fontSize = 13.sp,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    ).toSpanStyle(),
                    hoveredStyle = defaultTextStyle.copy(
                        fontSize = 13.sp,
                        color = Color.White,
                        textDecoration = TextDecoration.Underline
                    ).toSpanStyle()
                ),
                table = defaultTextStyle,
            ),
        )
    }
}

@Composable
fun MediaMessageContent(
    state: MessagesComponent.State,
    component: MessagesComponent,
    channel: String,
    message: Message,
) {
    when (message.type) {
        MessageType.Text -> error("Should not be here!")
        MessageType.Photo -> {
            PhotoMessageContent(channel = channel, message = message)
        }

        else -> {
            Card {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Media Type: ${message.type.toString()}", fontSize = 14.sp)
                    Text(message.media.orEmpty(), fontSize = 14.sp)
                    val actionButtons = @Composable {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    openFile(
                                        file = getDownloadDirectory().resolve("tg_scrapper")
                                            .resolve(message.media.orEmpty())
                                    )
                                },
                            ) {
                                Text("Open")
                            }
                            Button(
                                onClick = {
                                    shareFile(
                                        file = getDownloadDirectory().resolve("tg_scrapper")
                                            .resolve(message.media.orEmpty())
                                    )
                                },
                            ) {
                                Text("Share")
                            }
                        }
                    }
                    if (component.isMessageDownloaded(message)) {
                        actionButtons()
                    } else {
                        val download = state.downloads.get(message.id)
                        val downloadStatus = download?.status
                        AnimatedContent(
                            targetState = downloadStatus
                        ) { status ->
                            when (status) {
                                MessagesComponent.DownloadStatus.Downloaded -> {
                                    actionButtons()
                                }

                                MessagesComponent.DownloadStatus.Downloading -> {
                                    Row {
                                        Text(
                                            "Downloading (${download?.progress})"
                                        )
                                    }
                                }

                                null -> {
                                    Button(
                                        onClick = {
                                            component.downloadMessage(message)
                                        },
                                    ) {
                                        Text("Tap to download!")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.heightIn(12.dp))
            HorizontalDivider()
            Spacer(Modifier.heightIn(12.dp))
        }
    }
    if (message.caption != null) {
        TextMessageContent(
            content = message.caption
        )
    }
}

@Composable
fun PhotoMessageContent(channel: String, message: Message) {
    val context = LocalPlatformContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .crossfade(400)
            .data(
                EndPoints.media(
                    channel = channel,
                    media = message.media.orEmpty()
                )
            )
            .build(),
        contentDescription = null,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
            .heightIn(max = 400.dp)
            .fillMaxWidth()
    )
}