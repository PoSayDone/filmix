package io.github.posaydone.filmix.mobile.ui.screen.settingsScreen.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.posaydone.filmix.mobile.ui.common.settings.SettingItem
import io.github.posaydone.filmix.mobile.ui.common.settings.SettingsGroup

@Composable
fun SettingScreenContent(
    paddingValues: PaddingValues,
    values: Map<String, String>,
    currentValue: String,
    updateValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        SettingsGroup(
            items = values.map { item ->
                {
                    SettingItem(title = item.value, onClick = {
                        updateValue(item.key)
                    }, trailingContent = {
                        if (currentValue == item.key) Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Checked",
                        )
                    })
                }
            },
        )
    }
}