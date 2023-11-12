package com.maryya.ingredible

import CameraPreview
import SettingsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "mainScreen") {
                composable("mainScreen") { MainScreen(navController) }
                composable("settingsScreen") { SettingsScreen() }
            }
        }
    }

    @Composable
    fun MainScreen(navController: NavController) {
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(modifier = Modifier.fillMaxSize()) // Camera view as background

        CameraView() // Placeholder for camera view
            Spacer(modifier = Modifier.height(16.dp))

            // Gear (Settings) button at the top right
            IconButton(
                onClick = { navController.navigate("settingsScreen") },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp)
                    .background(color = Color.LightGray, shape = CircleShape)

            ) {
                Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.DarkGray)
            }

            // Eye button at the bottom center
            // Adjusted alignment for bottom center
            IconButton(
                onClick = { /* TODO: Handle eye click */ },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .background(color = Color.DarkGray, shape = CircleShape)
                    .padding(16.dp)  // Increase inner padding to make the button larger
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = "Visibility", tint = Color.White, modifier = Modifier.size(32.dp)  // Increase icon size
                )
            }
        }
    }

    @Composable
    fun CameraView() {
        // Placeholder for camera view
        // You'll replace this with actual CameraX implementation
        Box(
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Camera Preview")
        }
    }
}