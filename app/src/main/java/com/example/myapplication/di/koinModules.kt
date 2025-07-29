package com.example.myapplication.di

import androidx.room.Room
import androidx.work.WorkManager
import com.example.ktor_client.ApiClient
import com.example.myapplication.MainViewModel
import com.example.myapplication.data.database.NoteDatabase
import com.example.myapplication.data.datasource.NoteTaskDataSource
import com.example.myapplication.data.repository.NoteRepository
import com.example.myapplication.data.repository.NoteRepositoryImpl
import com.example.myapplication.data.workmanager.worker.AddFavouriteNoteWorker
import com.example.myapplication.data.workmanager.worker.AddNoteWorker
import com.example.myapplication.data.workmanager.worker.DeleteFavouriteNoteWorker
import com.example.myapplication.data.workmanager.worker.DeleteNoteWorker
import com.example.myapplication.data.workmanager.worker.SyncNotesWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get()) }

    worker { SyncNotesWorker(get(), get(), androidContext(), get()) }
    worker { AddFavouriteNoteWorker(get(), androidContext(), get()) }
    worker { DeleteFavouriteNoteWorker(get(), androidContext(), get()) }
    worker { AddNoteWorker(get(), get(), androidContext(), get()) }
    worker { DeleteNoteWorker(get(), androidContext(), get()) }
}

val dataModule = module {
    single { Room.databaseBuilder(androidContext(),
        NoteDatabase::class.java, "notes-database"
    ).build() }

    factory { get<NoteDatabase>().noteDao() }

    factory {
        val workManager = WorkManager.getInstance(androidContext())
        NoteTaskDataSource(workManager)
    }

    factory<NoteRepository> { NoteRepositoryImpl(get(), get()) }
}

val networkModule = module {
    single { ApiClient() }
}