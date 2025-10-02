package io.github.posaydone.filmix.mobile.newNavigation.graph

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import io.github.posaydone.filmix.core.common.sharedViewModel.settings.ProfileScreenViewModel
import io.github.posaydone.filmix.mobile.newNavigation.screen.ProfileGraphData
import io.github.posaydone.filmix.mobile.ui.screen.profileScreen.ProfileScreen
import io.github.posaydone.filmix.mobile.ui.screen.profileScreen.settings.ServerLocationScreen
import io.github.posaydone.filmix.mobile.ui.screen.profileScreen.settings.StreamTypeScreen
import io.github.posaydone.filmix.mobile.ui.screen.profileScreen.settings.VideoQualityScreen

@Composable
fun ProfileGraph(
    viewModel: ProfileScreenViewModel = hiltViewModel(),
) {
    val backStack = rememberNavBackStack<ProfileGraphData>(ProfileGraphData.Profile)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ProfileGraphData.Profile> {
                ProfileScreen(
                    navigateToVideoQualityScreen = { backStack.add(ProfileGraphData.VideoQuality) },
                    navigateToVideoStreamTypeScreen = { backStack.add(ProfileGraphData.StreamType) },
                    navigateToVideoServerLocationScreen = { backStack.add(ProfileGraphData.ServerLocation) },
                    viewModel = viewModel
                )
            }
            entry<ProfileGraphData.VideoQuality> {
                VideoQualityScreen(
                    navigateBack = { backStack.removeAt(backStack.lastIndex) },
                    viewModel = viewModel
                )
            }
            entry<ProfileGraphData.StreamType> {
                StreamTypeScreen(
                    navigateBack = { backStack.removeAt(backStack.lastIndex) },
                    viewModel = viewModel
                )
            }
            entry<ProfileGraphData.ServerLocation> {
                ServerLocationScreen(
                    navigateBack = { backStack.removeAt(backStack.lastIndex) },
                    viewModel = viewModel
                )
            }
        })

}
