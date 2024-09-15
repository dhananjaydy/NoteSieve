package com.example.notesieve.ui.homescreen.composables.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorIndicator(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .size(size)
        .clip(CircleShape)
        .background(Color.Red))
    {
        Text(
            text = "!",
            color = Color.White,
            fontSize = (size.value / 2).sp,
            textAlign = TextAlign.Center
        )
    }
}