package com.example.notesieve.ui.homescreen.commons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.example.notesieve.R.string
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.utils.epochLongToString
import kotlinx.coroutines.delay

private val urlPattern =
    Regex("""((https?:\/\/)?(www\.)?[a-zA-Z0-9\-]+\.[a-zA-Z]{2,}(\S*)?)""")

fun buildTextWithLinks(
    text: String,
    onUrlClick: (String) -> Unit
): AnnotatedString {

    val matches = urlPattern.findAll(text)

    return buildAnnotatedString {

        var currentIndex = 0

        matches.forEach { match ->

            val start = match.range.first
            val end = match.range.last + 1
            var url = match.value

            append(text.substring(currentIndex, start))

            if (!url.startsWith("http")) {
                url = "https://$url"
            }

            withLink(
                LinkAnnotation.Url(
                    url = url,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = Color(0xFF1E88E5),
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = {
                        onUrlClick(url)
                    }
                )
            ) {
                append(match.value)
            }

            currentIndex = end
        }

        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}

@Composable
fun NotificationItem(
    noteSieveModel: NoteSieveModel,
    onStarClick: (Int, Boolean) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    var isVisible by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val annotatedContent = remember(noteSieveModel.notificationContent) {
        buildTextWithLinks(
            text = noteSieveModel.notificationContent,
            onUrlClick = onUrlClick
        )
    }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(300)
            onDeleteClick(noteSieveModel.id)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut(tween(300)) +
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
    ) {

        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple()
                ) {
                    onBodyClick(noteSieveModel.id, noteSieveModel.showOptions)
                },
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.Top
                ) {

                    AppIcon(
                        packageName = noteSieveModel.packageName,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 8.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = noteSieveModel.appName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            val icon =
                                if (noteSieveModel.isFavorite)
                                    Icons.Default.Star
                                else Icons.Outlined.StarOutline

                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    onStarClick(
                                        noteSieveModel.id,
                                        noteSieveModel.isFavorite
                                    )
                                }
                            )
                        }

                        Text(
                            text = noteSieveModel.notificationTitle,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = annotatedContent,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(Modifier.height(4.dp))
                        
                        Text(
                            text = context.epochLongToString(
                                noteSieveModel.timestamp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = noteSieveModel.showOptions,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {

                    Column {

                        val formattedContent = buildString {
                            appendLine(noteSieveModel.appName)
                            appendLine()
                            appendLine(noteSieveModel.notificationTitle)
                            appendLine()
                            appendLine(noteSieveModel.notificationContent)
                            appendLine()
                            append(
                                context.epochLongToString(
                                    noteSieveModel.timestamp
                                )
                            )
                        }

                        HorizontalDivider()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            OptionItem(
                                icon = Icons.Default.Share,
                                text = stringResource(string.share),
                                onClick = { onShareClick(formattedContent) },
                                modifier = Modifier.weight(1f)
                            )

                            VerticalDivider()

                            OptionItem(
                                icon = Icons.Default.Delete,
                                text = stringResource(string.delete),
                                onClick = {
                                    isVisible = false
                                    isDeleting = true
                                },
                                modifier = Modifier.weight(1f)
                            )

                            VerticalDivider()

                            OptionItem(
                                icon = Icons.Default.ContentCopy,
                                text = stringResource(string.copy),
                                onClick = { onCopyClick(formattedContent) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}