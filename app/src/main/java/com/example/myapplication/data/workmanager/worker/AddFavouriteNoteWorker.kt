package com.example.myapplication.data.workmanager.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ktor_client.ApiClient
import com.example.myapplication.data.dao.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException

class AddFavouriteNoteWorker(private val apiClient: ApiClient,
                             context: Context,
                             params: WorkerParameters): CoroutineWorker(context, params) {
    companion object {
        const val NOTE_ID_KEY = "NOTE_ID"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val noteId = inputData.getLong(NOTE_ID_KEY, -1)

        if (noteId > 0) {
            try {
                apiClient.addFavourite(noteId)

                Result.success()
            } catch (_: ConnectException) {
                Log.w("AddFavourite", "Failed to connect to backend. Retrying")
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }
}