package com.example.myapplication.data.workmanager.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ktor_client.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddFavouriteNoteWorker(private val apiClient: ApiClient,
                             context: Context,
                             params: WorkerParameters): CoroutineWorker(context, params) {
    companion object {
        const val NOTE_ID_KEY = "NOTE_ID"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val noteId = inputData.getLong(NOTE_ID_KEY, -1)

        if (noteId > 0) {
            apiClient.addFavourite(noteId)

            Result.success()
        } else {
            Result.failure()
        }
    }
}