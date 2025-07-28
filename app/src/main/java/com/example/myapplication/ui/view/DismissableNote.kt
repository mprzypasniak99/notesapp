package com.example.myapplication.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.SwipeToDismissBoxValue.Settled
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.NoteModel

@Composable
fun DismissableNote(modifier: Modifier = Modifier,
                    note: NoteModel,
                    onDismiss: () -> Unit,
                    onFavouriteToggle: (Boolean) -> Unit) {
    val density = LocalDensity.current
    val confirmValueChange = { it: SwipeToDismissBoxValue -> it == StartToEnd }
    val positionalThreshold = { it: Float -> it / 3 * 2}
    val dismissState =
        rememberSaveable(
            note.id,
            saver = SwipeToDismissBoxState.Saver(confirmValueChange, positionalThreshold, density),
        ) {
            SwipeToDismissBoxState(Settled, density, confirmValueChange, positionalThreshold)
        }
    val backgroundColor by
    rememberUpdatedState(
        when (dismissState.dismissDirection) {
            StartToEnd -> lerp(Color.Transparent, Color.Red, dismissState.progress)
            else -> Color.Transparent
        }
    )
    if (dismissState.currentValue == StartToEnd) {
        LaunchedEffect(note.id) {
            onDismiss()
        }
    }

    SwipeToDismissBox(
        modifier = modifier.fillMaxWidth(),
        state = dismissState,
        enableDismissFromEndToStart = false,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(backgroundColor),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        },
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp).align(Alignment.End),
                    checked = note.favourite,
                    enabled = note.backendId != null,
                    onCheckedChange = onFavouriteToggle,
                    thumbContent = {
                        Icon(
                            imageVector = if (note.favourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    })

                Text(modifier = Modifier.padding(16.dp), text = note.content)
            }
        }
    }
}