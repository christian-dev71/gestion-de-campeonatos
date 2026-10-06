package com.example.gestiondecampeonatos.ui.components

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gestiondecampeonatos.ui.navigation.TopLevelDestination

private val SelectedMenuColor = Color(0xFF3F51B5)

@Composable
fun ChampionshipBottomBar(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = Color(0xFFE5E7EB))
        NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
            TopLevelDestination.entries.forEach { destination ->
                NavigationBarItem(
                    selected = destination == selectedDestination,
                    onClick = { onDestinationSelected(destination) },
                    icon = {
                        Icon(
                            painter = painterResource(destination.iconRes),
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(destination.labelRes)) },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SelectedMenuColor,
                        selectedTextColor = SelectedMenuColor,
                        indicatorColor = SelectedMenuColor.copy(alpha = 0.10f),
                        unselectedIconColor = Color(0xFF4B5563),
                        unselectedTextColor = Color(0xFF4B5563),
                    ),
                )
            }
        }
    }
}
