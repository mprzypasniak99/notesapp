package com.example.myapplication.data.model

data class NoteModel(
    val id: Long,
    val backendId: Long?,
    val content: String,
    val favourite: Boolean
)
