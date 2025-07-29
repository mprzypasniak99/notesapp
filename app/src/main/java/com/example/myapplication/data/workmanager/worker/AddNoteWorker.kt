package com.example.myapplication.data.workmanager.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ktor_client.ApiClient
import com.example.models.dto.CreateNoteBody
import com.example.myapplication.data.dao.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException

class AddNoteWorker(private val apiClient: ApiClient,
                    private val noteDao: NoteDao,
                    context: Context,
                    params: WorkerParameters): CoroutineWorker(context, params) {
    companion object {
        const val NOTE_ID_KEY = "NOTE_ID"
        const val NOTE_CONTENT_KEY = "NOTE_CONTENT"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val noteId = inputData.getLong(NOTE_ID_KEY, -1)
        val noteContent = inputData.getString(NOTE_CONTENT_KEY)

        if (noteId > 0 && noteContent != null) {
            try {
                val body = CreateNoteBody(noteContent)
                apiClient.addNote(body)

                noteDao.deleteById(noteId)
                Result.success()
            } catch (_: ConnectException) {
                Log.w("AddNote", "Failed to connect to backend. Retrying")
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }
}