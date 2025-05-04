package io.github.posaydone.filmix.tv.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.navigation.NavHostController

private val LocalLastFocusedItemPerDestination = compositionLocalOf<MutableMap<String, String>?> { null }
private val LocalFocusTransferredOnLaunch = compositionLocalOf<MutableState<Boolean>?> { null }
private val LocalNavHostController = compositionLocalOf<NavHostController?> { null }

@Composable
fun LocalLastFocusedItemPerDestinationProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLastFocusedItemPerDestination provides remember { mutableMapOf() }, content = content)
}

@Composable
fun LocalFocusTransferredOnLaunchProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFocusTransferredOnLaunch provides remember { mutableStateOf(false) }, content = content)
}

@Composable
fun LocalNavHostControllerProvider(navHostController: NavHostController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalNavHostController provides navHostController, content = content)
}

@Composable
fun useLocalLastFocusedItemPerDestination(): MutableMap<String, String> {
    return LocalLastFocusedItemPerDestination.current ?: throw RuntimeException("Please wrap your app with LocalLastFocusedItemPerDestinationProvider")
}

@Composable
fun useLocalFocusTransferredOnLaunch(): MutableState<Boolean> {
    return LocalFocusTransferredOnLaunch.current ?: throw RuntimeException("Please wrap your app with LocalLastFocusedItemPerDestinationProvider")
}

@Composable
fun useLocalNavHostController(): NavHostController {
    return LocalNavHostController.current ?: throw RuntimeException("Please wrap your app with LocalNavHostControllerProvider")
}


data class FocusRequesterModifiers(
    val parentModifier: Modifier,
    val childModifier: Modifier
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun createInitialFocusRestorerModifiers(): FocusRequesterModifiers {
    val focusRequester = remember { FocusRequester() }
    val childFocusRequester = remember { FocusRequester() }

    val parentModifier = Modifier
        .focusRequester(focusRequester)
        .focusProperties {
            onExit = {
                focusRequester.saveFocusedChild()
                FocusRequester.Default
            }
            onEnter = {
                if (focusRequester.restoreFocusedChild()) FocusRequester.Cancel
                else childFocusRequester
            }
        }

    val childModifier = Modifier.focusRequester(childFocusRequester)

    return FocusRequesterModifiers(parentModifier, childModifier)
}

@Composable
fun Modifier.focusOnMount(itemKey: String): Modifier {
    val focusRequester = remember { FocusRequester() }
    val isInitialFocusTransferred = useLocalFocusTransferredOnLaunch()
    val lastFocusedItemPerDestination = useLocalLastFocusedItemPerDestination()
    val navHostController = useLocalNavHostController()
    val currentDestination = remember(navHostController) { navHostController.currentDestination?.route }

    return this
        .focusRequester(focusRequester)
        .onGloballyPositioned {
            val lastFocusedKey = lastFocusedItemPerDestination[currentDestination]
            if (!isInitialFocusTransferred.value && lastFocusedKey == itemKey) {
                focusRequester.requestFocus()
                isInitialFocusTransferred.value = true
            }
        }
        .onFocusChanged {
            if (it.isFocused) {
                lastFocusedItemPerDestination[currentDestination ?: ""] = itemKey
                isInitialFocusTransferred.value = true
            }
        }
}
