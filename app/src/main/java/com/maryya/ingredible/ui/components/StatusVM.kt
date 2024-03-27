package com.maryya.ingredible.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusVm (statusText: String) {

    // Status text Composable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add padding to ensure the text is not right at the edge
        contentAlignment = Alignment.BottomCenter // Aligns the content to the bottom center pera mela u
    ) {
        Text(
            text = statusText,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }


}