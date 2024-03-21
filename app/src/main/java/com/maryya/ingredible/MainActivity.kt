package com.maryya.ingredible

import CameraPreview
import SettingsScreen
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@ExperimentalCamera2Interop class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val factory = SharedViewModelFactory(LocalContext.current.applicationContext as Application);
            val viewModel: SharedViewModel = viewModel(factory = factory)

            NavHost(navController = navController, startDestination = "mainScreen") {
                composable("mainScreen") { MainScreen(navController, viewModel) }
                composable("settingsScreen") { SettingsScreen(viewModel) }
            }
        }
    }

    @Composable
    fun MainScreen(navController: NavController, viewModel: SharedViewModel) {
        var isOCRActive by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(modifier = Modifier.fillMaxSize(), isOCRActive = isOCRActive, viewModel = viewModel) // Camera view as background
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
                onClick = {
                    Log.d("Eye Click", "OCR Active: $isOCRActive")

                    isOCRActive = !isOCRActive
                          },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .background(color = Color.DarkGray, shape = CircleShape)
                    .padding(16.dp)  // Increase inner padding to make the button larger
            ) {
                Icon(
                    imageVector = if (isOCRActive) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (isOCRActive) "Stop OCR" else "Start OCR",
                    tint = Color.White
                )
            }
        }
    }
}