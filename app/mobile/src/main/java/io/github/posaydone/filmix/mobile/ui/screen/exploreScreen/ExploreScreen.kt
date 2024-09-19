package io.github.posaydone.filmix.mobile.ui.screen.exploreScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.mobile.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    paddingValues: PaddingValues,
    navController: NavHostController,
) {
    Scaffold(topBar = {
        ExploreSearchBar { navController.navigate(Screens.Main.SearchResults(it)) }
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            content = {
                LazyColumn {
                }
            }
        )
    }
}