package com.example.myapplication.data.workmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ktor_client.ApiClient
import com.example.models.dto.GetNoteBody
import com.example.myapplication.data.dao.NoteDao
import com.example.myapplication.data.toNoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncNotesWorker(private val apiClient: ApiClient,
                      private val noteDao: NoteDao,
                      context: Context,
                      params: WorkerParameters): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val notes = apiClient.getNotes().map(GetNoteBody::toNoteEntity)

            noteDao.upsertNotesFromBackend(notes)
        } catch (_: Exception) {
            Result.retry()
        }

        Result.success()
    }
}