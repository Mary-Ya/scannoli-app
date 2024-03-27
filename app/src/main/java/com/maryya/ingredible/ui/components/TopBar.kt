package com.maryya.ingredible.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TopBar(navController: NavController, onSheetToggle: () -> Unit) {
    TopAppBar(
        title = { Text("Scannoli") },
        navigationIcon = {
            IconButton(onClick = onSheetToggle) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate("settingsScreen") }) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings")
            }
        }
    )
}