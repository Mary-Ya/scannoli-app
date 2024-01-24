package com.maryya.ingredible

import SharedViewModel
import WordTriple
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WordPill(wordTriple: WordTriple, viewModel: SharedViewModel) {
    val color = viewModel.getColorForItem(wordTriple.word)

    val safeCurrentWord = wordTriple.word ?: ""
    val safePrevText = wordTriple.prevWord ?: ""
    val safeNextText = wordTriple.nextWord ?: ""

    // Display previous, current, and next words
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = safePrevText,
                maxLines = 1, // Ensures text is one line
                overflow = TextOverflow.Visible, // Text will be truncated with an ellipsis if it overflows
                modifier = Modifier
                    .background(color.copy(alpha = 0.3f), RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 15.sp // Adjust font size as needed

            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = safeCurrentWord,
                maxLines = 1, // Ensures text is one line
                overflow = TextOverflow.Visible, // Text will be truncated with an ellipsis if it overflows
                modifier = Modifier
                    .background(color, RoundedCornerShape(50.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 22.5.sp // Larger font for the current word
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = safeNextText,
                maxLines = 1, // Ensures text is one line
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .background(color.copy(alpha = 0.3f), RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 15.sp // Adjust font size as needed
            )
        }
    }
}