package com.example.notesieve.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun GridFilledIcon() {
    Icon(
        imageVector = gridFilledIcon,
        contentDescription = "Grid Filled Icon",
        tint = Color.Black,
        modifier = androidx.compose.ui.Modifier.size(24.dp)
    )
}

@Composable
fun GridOutlinedIcon() {
    Icon(
        imageVector = gridOutlinedIcon,
        contentDescription = "Grid Outlined Icon",
        tint = Color.Black,
        modifier = androidx.compose.ui.Modifier.size(24.dp)
    )
}

@Composable
fun ListFilledIcon() {
    Icon(
        imageVector = listFilledIcon,
        contentDescription = "List Filled Icon",
        tint = Color.Black,
        modifier = androidx.compose.ui.Modifier.size(24.dp)
    )
}

@Composable
fun ListOutlinedIcon() {
    Icon(
        imageVector = listOutlinedIcon,
        contentDescription = "List Outlined Icon",
        tint = Color.Black,
        modifier = androidx.compose.ui.Modifier.size(24.dp)
    )
}

val gridFilledIcon: ImageVector
    get() = ImageVector.Builder(
        name = "GridFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = Brush.linearGradient(listOf(Color.Black, Color.Black)), stroke = null) {
            moveTo(4f, 4f)
            lineTo(10f, 4f)
            lineTo(10f, 10f)
            lineTo(4f, 10f)
            close()

            moveTo(14f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 10f)
            lineTo(14f, 10f)
            close()

            moveTo(4f, 14f)
            lineTo(10f, 14f)
            lineTo(10f, 20f)
            lineTo(4f, 20f)
            close()

            moveTo(14f, 14f)
            lineTo(20f, 14f)
            lineTo(20f, 20f)
            lineTo(14f, 20f)
            close()
        }
    }.build()

val gridOutlinedIcon: ImageVector
    get() = ImageVector.Builder(
        name = "GridOutlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(stroke = Brush.linearGradient(listOf(Color.Black, Color.Black)), strokeLineWidth = 2f) {
            moveTo(4f, 4f)
            lineTo(10f, 4f)
            lineTo(10f, 10f)
            lineTo(4f, 10f)
            close()

            moveTo(14f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 10f)
            lineTo(14f, 10f)
            close()

            moveTo(4f, 14f)
            lineTo(10f, 14f)
            lineTo(10f, 20f)
            lineTo(4f, 20f)
            close()

            moveTo(14f, 14f)
            lineTo(20f, 14f)
            lineTo(20f, 20f)
            lineTo(14f, 20f)
            close()
        }
    }.build()

val listOutlinedIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ListFilled",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = Brush.linearGradient(listOf(Color.Black, Color.Black)), stroke = null) {
            moveTo(4f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 6f)
            lineTo(4f, 6f)
            close()

            moveTo(4f, 10f)
            lineTo(20f, 10f)
            lineTo(20f, 12f)
            lineTo(4f, 12f)
            close()

            moveTo(4f, 16f)
            lineTo(20f, 16f)
            lineTo(20f, 18f)
            lineTo(4f, 18f)
            close()
        }
    }.build()

val listFilledIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ListOutlined",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(stroke = Brush.linearGradient(listOf(Color.Black, Color.Black)), strokeLineWidth = 2f) {
            moveTo(4f, 4f)
            lineTo(20f, 4f)
            lineTo(20f, 6f)
            lineTo(4f, 6f)
            close()

            moveTo(4f, 10f)
            lineTo(20f, 10f)
            lineTo(20f, 12f)
            lineTo(4f, 12f)
            close()

            moveTo(4f, 16f)
            lineTo(20f, 16f)
            lineTo(20f, 18f)
            lineTo(4f, 18f)
            close()
        }
    }.build()

@Preview
@Composable
fun PreviewIcons() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            GridFilledIcon()
            GridOutlinedIcon()
            ListFilledIcon()
            ListOutlinedIcon()
        }
    }
}
