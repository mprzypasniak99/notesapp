package com.example.myapplication

import com.example.myapplication.data.model.NoteModel

enum class NoteFilter(val label: String, val filterFunction: (NoteModel) -> Boolean) {
    ALL("Wszystkie", { true }),
    FAVOURITES("Ulubione", { it.favourite })
}

data class MainUiState(
    val notes: List<NoteModel> = emptyList(),
    val newNoteInput: String = "",
    val filter: NoteFilter = NoteFilter.ALL
) {
    fun updateNotesList(notes: List<NoteModel>): MainUiState =
        copy(notes = notes.filter(filter.filterFunction))

    fun updateNewNoteInput(input: String): MainUiState = copy(newNoteInput = input)

    fun updateFilter(filter: NoteFilter, allNotes: List<NoteModel>): MainUiState =
        copy(
            filter = filter,
            notes = allNotes.filter(filter.filterFunction)
        )
}
