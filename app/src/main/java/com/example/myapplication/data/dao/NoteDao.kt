package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.myapplication.data.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE backendId = :backendId")
    fun getByBackendId(backendId: Long): NoteEntity?

    @Insert(onConflict = REPLACE)
    fun insert(note: NoteEntity)

    @Insert(NoteEntity::class, REPLACE)
    fun insertAll(notes: List<NoteEntity>)

    @Delete
    fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    fun deleteById(id: Long)

    @Update
    fun update(note: NoteEntity)

    @Transaction
    fun upsertNotesFromBackend(notes: List<NoteEntity>) {
        for (note in notes) {
            if (note.backendId != null) {
                val existingNote = getByBackendId(note.backendId)

                if (existingNote == null) {
                    insert(note)
                } else {
                    val updatedNote = existingNote.copy(
                        backendId = note.backendId,
                        content = note.content,
                        favourite = note.favourite
                    )

                    update(updatedNote)
                }
            }
        }
    }
}