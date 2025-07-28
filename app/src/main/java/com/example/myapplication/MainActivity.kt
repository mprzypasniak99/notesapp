package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.view.WindowCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.view.NewNoteField
import com.example.myapplication.ui.view.NoteFilterButtons
import com.example.myapplication.ui.view.NotesList
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()
                val notes = uiState.notes
                val input = uiState.newNoteInput
                val filter = uiState.filter

                val layoutDirection = LocalLayoutDirection.current

                val gestureInsets = WindowInsets.safeGestures.asPaddingValues()
                val imeInset = WindowInsets.ime.asPaddingValues()
                Scaffold { contentPadding ->
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            PaddingValues(
                                top = contentPadding.calculateTopPadding(),
                                start = gestureInsets.calculateStartPadding(layoutDirection),
                                end = gestureInsets.calculateEndPadding(layoutDirection),
                                bottom = contentPadding.calculateBottomPadding() +
                                        imeInset.calculateBottomPadding()
                            )
                        )) {

                        NoteFilterButtons(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            selectedFilter = filter,
                            onClick = viewModel::changeFilter
                        )

                        NotesList(
                            modifier = Modifier.weight(1f),
                            notes = notes,
                            onDismissNote = viewModel::deleteNote,
                            onFavouriteToggle = viewModel::updateFavourite
                        )

                        NewNoteField(
                            input = input,
                            onValueChange = viewModel::updateNoteInput,
                            onSendNote = viewModel::addNote
                        )
                    }
                }
            }
        }
    }
}
