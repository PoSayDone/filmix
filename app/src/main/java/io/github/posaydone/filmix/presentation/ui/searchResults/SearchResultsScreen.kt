@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.posaydone.filmix.presentation.ui.searchResults

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.posaydone.filmix.R
import io.github.posaydone.filmix.presentation.common.mobile.ShowsGrid

@Composable
fun SearchResultsScreen(
    query: String,
    paddingValues: PaddingValues,
    navController: NavHostController,
    viewModel: SearchResultsViewModel = hiltViewModel(),
) {
    val shows by viewModel.shows.collectAsStateWithLifecycle()

    Scaffold(
        topBar =
        {
            TopAppBar({},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            contentDescription = "Navback icon",
                            painter = painterResource(R.drawable.ic_arrow_back)
                        )
                    }
                })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            ShowsGrid(shows, navController)
        }
    }
}