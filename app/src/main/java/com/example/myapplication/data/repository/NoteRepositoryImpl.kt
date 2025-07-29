package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.NoteDao
import com.example.myapplication.data.datasource.NoteTaskDataSource
import com.example.myapplication.data.entity.NoteEntity
import com.example.myapplication.data.model.NoteModel
import com.example.myapplication.data.toNoteEntity
import com.example.myapplication.data.toNoteModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NoteRepositoryImpl(
    private val noteDao: NoteDao,
    private val noteTaskDataSource: NoteTaskDataSource
): NoteRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getNotesStream(): Flow<List<NoteModel>> {
        return noteDao.getAll().map {
            it.map(NoteEntity::toNoteModel)
        }
    }

    override fun refreshNotes() {
        noteTaskDataSource.fetchNotes()
    }

    override fun addFavourite(note: NoteModel) {
        scope.launch {
            val updatedNote = note.copy(favourite = true).toNoteEntity()
            noteDao.update(updatedNote)

            if (updatedNote.backendId != null) {
                noteTaskDataSource.addFavourite(updatedNote.backendId)
            }
        }
    }

    override fun deleteFavourite(note: NoteModel) {
        scope.launch {
            val updatedNote = note.copy(favourite = false).toNoteEntity()
            noteDao.update(updatedNote)

            if (updatedNote.backendId != null) {
                noteTaskDataSource.deleteFavourite(updatedNote.backendId)
            }
        }
    }

    override fun addNote(noteContent: String) {
        scope.launch {
            val id = System.currentTimeMillis()
            val note = NoteEntity(
                id = id,
                content = noteContent,
                favourite = false
            )

            noteDao.insert(note)

            noteTaskDataSource.addNote(id, noteContent)
        }
    }

    override fun deleteNote(note: NoteModel) {
        scope.launch {
            val updatedNote = note.toNoteEntity()
            noteDao.delete(updatedNote)

            if (updatedNote.backendId != null) {
                noteTaskDataSource.deleteNote(updatedNote.backendId)
            } else {
                noteTaskDataSource.cancelWorkForNoteId(note.id)
            }
        }
    }
}