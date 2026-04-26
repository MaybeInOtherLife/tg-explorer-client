package io.github.maybeinotherlife.ui

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import io.github.maybeinotherlife.components.RootComponent
import io.github.maybeinotherlife.ui.screens.ChannelListScreen
import io.github.maybeinotherlife.ui.screens.MessagesScreen

@Composable
fun RootContent(component: RootComponent){
    Children(
        stack = component.stack,
        animation = stackAnimation(fade()+ slide())
    ){
        when(val route = it.instance){
            is RootComponent.Child.ChannelList -> ChannelListScreen(route.component)
            is RootComponent.Child.Messages -> MessagesScreen(
                channel = route.channel,
                component = route.component
            )
        }
    }
}