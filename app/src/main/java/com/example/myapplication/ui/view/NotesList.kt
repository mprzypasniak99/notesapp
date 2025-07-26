package com.example.myapplication.ui.view

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.NoteModel

@Composable
fun NotesList(modifier: Modifier = Modifier,
              contentPadding: PaddingValues = PaddingValues(0.dp),
              notes: List<NoteModel>,
              onDismissNote: (NoteModel) -> Unit,
              onFavouriteToggle: (NoteModel, Boolean) -> Unit) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(notes, key = { it.id }) { note ->
            DismissableNote(
                modifier = Modifier.animateItem(),
                note = note,
                onDismiss = {
                    onDismissNote(note)
                }, onFavouriteToggle = {
                    onFavouriteToggle(note, it)
                })
        }
    }
}