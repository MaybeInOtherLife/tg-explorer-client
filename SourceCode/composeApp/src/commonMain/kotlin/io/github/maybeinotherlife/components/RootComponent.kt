package io.github.maybeinotherlife.components

import androidx.datastore.preferences.core.edit
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.maybeinotherlife.models.Channel
import io.github.maybeinotherlife.navigation.Route
import io.github.maybeinotherlife.utils.createDataStore
import io.github.maybeinotherlife.utils.getDataStorePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


val ComponentContext.componentScope: CoroutineScope
    get() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        lifecycle.doOnDestroy { scope.cancel() }
        return scope
    }
class RootComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext{
    private val navigation = StackNavigation<Route>()

    val stack = childStack(
        source = navigation,
        serializer = Route.serializer(),
        initialConfiguration = Route.ChannelList,
        handleBackButton = true,
        childFactory = ::child,
    )

    private fun child(route: Route, componentContext: ComponentContext): Child {
        return when(route){
            Route.ChannelList -> Child.ChannelList(
                component = ChannelListComponent(
                    componentContext = componentContext,
                    onChannelClick = { channel->
                        componentScope.launch {
                            channel.lastMessage?.id?.let { lastMessageId->
                                datastore.edit {
                                    it[getDatastoreKeyForChannel(channel = channel)] = lastMessageId
                                }
                            }

                        }
                        navigation.push(Route.Messages(channel = channel))
                    }
                )
            )
            is Route.Messages -> Child.Messages(
                channel = route.channel,
                component = MessagesComponent(
                    componentContext = componentContext,
                    onBack = {
                        navigation.pop()
                    }
                )
            )
        }
    }

    sealed class Child{
        data class ChannelList(val component: ChannelListComponent):Child()
        data class Messages(val channel: Channel, val component: MessagesComponent):Child()
    }
}