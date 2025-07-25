package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.myapplication.data.model.NoteModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApplicationTheme {
                val notes by viewModel.notes.collectAsState()

                val layoutDirection = LocalLayoutDirection.current

                val gestureInsets = WindowInsets.safeGestures.asPaddingValues()
                val statusBarInset = WindowInsets.statusBars.asPaddingValues()
                val imeInset = WindowInsets.ime.asPaddingValues()
                val navigationInset = WindowInsets.navigationBars.asPaddingValues()
                Column(modifier = Modifier.fillMaxSize()) {
                    var input by remember { mutableStateOf("") }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding =
                            PaddingValues(
                                top = statusBarInset.calculateTopPadding(),
                                start = gestureInsets.calculateStartPadding(layoutDirection),
                                end = gestureInsets.calculateEndPadding(layoutDirection),
                            ),
                    ) {
                        items(notes) { note ->
                            DismissableNote(
                                modifier = Modifier.animateItem(),
                                note = note,
                                onDismiss = {
                                    viewModel.deleteNote(note)
                                }, onFavouriteToggle = {
                                    viewModel.updateFavourite(note.id, it)
                                })
                        }
                    }

                    TextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    PaddingValues(
                                        start =
                                            gestureInsets.calculateStartPadding(layoutDirection),
                                        end = gestureInsets.calculateEndPadding(layoutDirection),
                                        bottom =
                                            imeInset.calculateBottomPadding() +
                                                    navigationInset.calculateBottomPadding(),
                                    )
                                ),
                        value = input,
                        onValueChange = { input = it },
                        label = { Text("Treść notatki") },
                        keyboardActions =
                            KeyboardActions(
                                onSend = {
                                    if (input.isNotBlank()) {
                                        viewModel.addNote(input)
                                        input = ""
                                    }
                                }
                            ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    )
                }
            }
        }
    }
}

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
