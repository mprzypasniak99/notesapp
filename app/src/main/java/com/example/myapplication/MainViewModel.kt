package com.example.myapplication

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.NoteModel
import com.example.myapplication.data.repository.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainViewModel(
    private val noteRepository: NoteRepository
) : ViewModel() {
    private var notes = emptyList<NoteModel>()
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        observeNotesList()
        loadNotes()
    }

    private fun loadNotes() {
        noteRepository.refreshNotes()
    }

    fun addNote() {
        uiState.value.newNoteInput.let { note ->
            if (note.isNotBlank()) {
                noteRepository.addNote(note)
                _uiState.value = _uiState.value.updateNewNoteInput("")
            }
        }
    }

    fun deleteNote(note: NoteModel) {
        noteRepository.deleteNote(note)
    }

    fun updateFavourite(note: NoteModel, isFavourite: Boolean) {
        if (isFavourite) addFavourite(note) else deleteFavourite(note)
    }

    private fun addFavourite(note: NoteModel) {
        noteRepository.addFavourite(note)
    }

    private fun deleteFavourite(note: NoteModel) {
        noteRepository.deleteFavourite(note)
    }

    fun updateNoteInput(note: String) {
        scope.launch {
            _uiState.value = _uiState.value.updateNewNoteInput(note)
        }
    }

    fun changeFilter(filter: NoteFilter) {
        if (filter != _uiState.value.filter)
            scope.launch {
                _uiState.value = _uiState.value.updateFilter(filter, notes)
            }
    }

    private fun observeNotesList() {
        scope.launch {
            noteRepository.getNotesStream().distinctUntilChanged().collect {
                notes = it
                _uiState.value = _uiState.value.updateNotesList(it)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}