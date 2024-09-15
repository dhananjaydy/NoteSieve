package com.example.notesieve.ui.homescreen.composables.commons

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun TextWithClickableLinks(
    text: String,
    modifier: Modifier = Modifier
) {

    val uriHandler = LocalUriHandler.current
    val linkColor = Color.Blue

    val annotatedString = buildAnnotatedString {
        val urlRegex = Regex(pattern = """(https?://\S+)""")
        var lastIndex = 0

        urlRegex.findAll(text).forEach { matchResult ->
            append(text.substring(lastIndex, matchResult.range.first))
            val linkText = matchResult.value
            pushStringAnnotation(tag = "URL", annotation = linkText)
            withStyle(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = linkColor
                )
            ) {
                append(linkText)
            }
            pop()

            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }

    }
    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        },
        modifier = modifier,
        style = TextStyle(fontSize = 15.sp)
    )
}
