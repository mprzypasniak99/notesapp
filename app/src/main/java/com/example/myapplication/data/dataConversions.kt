package com.example.myapplication.data

import com.example.models.dto.GetNoteBody
import com.example.myapplication.data.entity.NoteEntity
import com.example.myapplication.data.model.NoteModel

fun NoteEntity.toNoteModel() = NoteModel(
    id = id,
    backendId = backendId,
    content = content,
    favourite = favourite
)

fun NoteModel.toNoteEntity() = NoteEntity(
    id = id,
    backendId = backendId,
    content = content,
    favourite = favourite
)

fun GetNoteBody.toNoteEntity() =
    NoteEntity(
        id = 0,
        backendId = id,
        content = content,
        favourite = favourite > 0
    )