package com.example.notesieve.ui.homescreen.commons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesieve.R.string
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.utils.epochLongToString
import kotlinx.coroutines.delay

@Composable
fun NotificationItem(
    noteSieveModel: NoteSieveModel,
    onStarClick: (Int, Boolean) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isVisible by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }

    LaunchedEffect(isDeleting) {
        if (isDeleting) {
            delay(300)
            onDeleteClick(noteSieveModel.id)
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut(animationSpec = tween(durationMillis = 900)) +
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .clickable { onBodyClick(noteSieveModel.id, noteSieveModel.showOptions) },
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    AppIcon(
                        packageName = noteSieveModel.packageName,
                        modifier = Modifier.size(40.dp)
                            .padding(end = 8.dp)
                    )
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            val icon = if (noteSieveModel.isFavorite)
                                Icons.Default.Star
                            else Icons.Outlined.StarOutline
                            Text(
                                text = noteSieveModel.appName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = noteSieveModel.notificationTitle,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        TextWithClickableLinks(
                            text = noteSieveModel.notificationContent
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = context.epochLongToString(noteSieveModel.timestamp),
                            fontSize = 12.sp
                        )
                    }
                }
                AnimatedVisibility(
                    visible = noteSieveModel.showOptions,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column {
                        val stringBuilder = StringBuilder().apply {
                            append(noteSieveModel.appName + "\n\n")
                            append(noteSieveModel.notificationTitle + "\n\n")
                            append(noteSieveModel.notificationContent + "\n\n")
                            append(context.epochLongToString(noteSieveModel.timestamp) + "\n\n")
                        }
                        val formattedContent = stringBuilder.toString()
                        HorizontalDivider(thickness = 1.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OptionItem(
                                icon = Icons.Default.Share,
                                text = stringResource(string.share),
                                onClick = { onShareClick(formattedContent) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )
                            OptionItem(
                                icon = Icons.Default.Delete,
                                text = stringResource(string.delete),
                                onClick = {
                                    isVisible = false
                                    isDeleting = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                            )
                            OptionItem(
                                icon = Icons.Default.ContentCopy,
                                text = stringResource(string.copy),
                                onClick = { onCopyClick(formattedContent) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun IconPreview() {
    Row {
        Icon(imageVector = Icons.Default.Star, contentDescription = null)
        Icon(imageVector = Icons.Outlined.StarOutline, contentDescription = null)
    }
}