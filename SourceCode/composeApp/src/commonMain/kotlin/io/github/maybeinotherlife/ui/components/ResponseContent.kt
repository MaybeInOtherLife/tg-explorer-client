package io.github.maybeinotherlife.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.maybeinotherlife.models.ApiResponse


@Composable
fun <T> ResponseContent(
    modifier: Modifier=Modifier,
    response: ApiResponse<T>?,
    doOnRetry: () -> Unit,
    loadingContent: @Composable () -> Unit = {
        CircularProgressIndicator()
    },
    successContent: @Composable BoxScope.(ApiResponse.Success<T>) -> Unit,
    errorContent: @Composable (ApiResponse.Error) -> Unit = {
        RequestError(onRetry = doOnRetry, error = it.error)
    },
) {
    AnimatedContent(
        modifier = modifier,
        targetState = response,
        transitionSpec = {
            fadeIn(tween(500)) togetherWith fadeOut(tween(500))
        },
        contentKey = {
            it?.stringState ?: "null"
        },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            when (it) {
                is ApiResponse.Error -> errorContent(it)
                ApiResponse.Loading -> loadingContent()
                is ApiResponse.Success -> successContent(it)
                null -> {}
            }
        }
    }
}
@Composable
fun RequestError(
    onRetry: () -> Unit,
    error: String? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load data",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        error?.let {
            Text(
                text = it,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Button(
            modifier = Modifier
                .width(130.dp)
                .height(40.dp),
            onClick = onRetry,
        ){
            Text("Retry")
        }
    }
}
