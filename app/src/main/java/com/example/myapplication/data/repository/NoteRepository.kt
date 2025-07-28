package com.example.myapplication.data.repository

import com.example.myapplication.data.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesStream(): Flow<List<NoteModel>>

    fun refreshNotes()
    fun addFavourite(note: NoteModel)
    fun deleteFavourite(note: NoteModel)
    fun addNote(noteContent: String)
    fun deleteNote(note: NoteModel)
}