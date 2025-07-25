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

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    init {
        loadNotes()
    }

    private fun loadNotes() {
        scope.launch {
            val notes = client.getNotes()
            _notes.value = notes.map { NoteModel(it.id, it.content, it.favourite > 0) }
        }
    }

    fun addNote(note: String) {
        scope.launch {
            client.addNote(CreateNoteBody(note))
            loadNotes()
        }
    }

    fun deleteNote(note: NoteModel) {
        scope.launch {
            client.deleteNote(note.id)
            loadNotes()
        }
    }

    fun updateFavourite(noteId: Long, isFavourite: Boolean) {
        if (isFavourite) addFavourite(noteId) else deleteFavourite(noteId)
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

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}