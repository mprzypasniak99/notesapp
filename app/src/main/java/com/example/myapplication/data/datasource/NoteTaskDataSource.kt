package com.example.myapplication.data.datasource

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.myapplication.data.workmanager.worker.AddFavouriteNoteWorker
import com.example.myapplication.data.workmanager.worker.AddNoteWorker
import com.example.myapplication.data.workmanager.worker.DeleteFavouriteNoteWorker
import com.example.myapplication.data.workmanager.worker.DeleteNoteWorker
import com.example.myapplication.data.workmanager.worker.SyncNotesWorker
import java.util.concurrent.TimeUnit

class NoteTaskDataSource(
    private val workManager: WorkManager
) {
    companion object {
        private const val FETCH_NOTES_WORK_NAME = "fetch_notes"
        private fun getWorkNameForNote(noteId: Long) = "work_note_$noteId"
    }
    private val constraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private inline fun <reified T: ListenableWorker> createRequestForWorker(inputData: Data = Data.EMPTY): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<T>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
    }

    fun fetchNotes() {
        workManager.enqueueUniqueWork(
            FETCH_NOTES_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            createRequestForWorker<SyncNotesWorker>()
        )
    }

    fun addFavourite(noteId: Long) {
        workManager
            .beginUniqueWork(
                getWorkNameForNote(noteId),
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                createRequestForWorker<AddFavouriteNoteWorker>(
                    workDataOf(AddFavouriteNoteWorker.NOTE_ID_KEY to noteId)
                ))
            .then(createRequestForWorker<SyncNotesWorker>())
            .enqueue()
    }

    fun deleteFavourite(noteId: Long) {
        workManager
            .beginUniqueWork(
                getWorkNameForNote(noteId),
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                createRequestForWorker<DeleteFavouriteNoteWorker>(
                    workDataOf(DeleteFavouriteNoteWorker.NOTE_ID_KEY to noteId)
                ))
            .then(createRequestForWorker<SyncNotesWorker>())
            .enqueue()
    }

    fun addNote(noteId: Long, noteContent: String) {
        workManager
            .beginUniqueWork(
                getWorkNameForNote(noteId),
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                createRequestForWorker<AddNoteWorker>(
                    workDataOf(AddNoteWorker.NOTE_ID_KEY to noteId,
                        AddNoteWorker.NOTE_CONTENT_KEY to noteContent)
                ))
            .then(createRequestForWorker<SyncNotesWorker>())
            .enqueue()
    }

    fun deleteNote(noteId: Long) {
        workManager
            .beginUniqueWork(
                getWorkNameForNote(noteId),
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                createRequestForWorker<DeleteNoteWorker>(
                    workDataOf(DeleteNoteWorker.NOTE_ID_KEY to noteId)
                ))
            .then(createRequestForWorker<SyncNotesWorker>())
            .enqueue()
    }
}