package com.example.myapplication

import androidx.lifecycle.ViewModel
import com.example.ktor_client.ApiClient
import com.example.models.dto.CreateNoteBody
import com.example.myapplication.data.model.NoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val client = ApiClient()
    private val _notes = MutableStateFlow<List<NoteModel>>(emptyList())
    val notes: StateFlow<List<NoteModel>> = _notes.asStateFlow()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        loadNotes()
    }

    private fun loadNotes() {
        scope.launch {
            val notes = client.getNotes()
            _notes.value = notes.map { NoteModel(it.id, it.content, it.favourite > 0) }
            _uiState.value = _uiState.value.updateNotesList(_notes.value)
        }
    }

    fun addNote() {
        uiState.value.newNoteInput.let { note ->
            if (note.isNotBlank())
            scope.launch {
                client.addNote(CreateNoteBody(note))
                _uiState.value = _uiState.value.updateNewNoteInput("")
                loadNotes()
            }
        }
    }

    fun deleteNote(note: NoteModel) {
        scope.launch {
            client.deleteNote(note.id)
            loadNotes()
        }
    }

    fun updateFavourite(note: NoteModel, isFavourite: Boolean) {
        if (isFavourite) addFavourite(note.id) else deleteFavourite(note.id)
    }

    private fun addFavourite(noteId: Long) {
        scope.launch {
            client.addFavourite(noteId)
            loadNotes()
        }
    }

    private fun deleteFavourite(noteId: Long) {
        scope.launch {
            client.deleteFavourite(noteId)
            loadNotes()
        }
    }

    fun updateNoteInput(note: String) {
        scope.launch {
            _uiState.value = _uiState.value.updateNewNoteInput(note)
        }
    }

    fun changeFilter(filter: NoteFilter) {
        if (filter != _uiState.value.filter)
            scope.launch {
                _uiState.value = _uiState.value.updateFilter(filter, _notes.value)
            }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}