package com.example.myapplication.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun NewNoteField(modifier: Modifier = Modifier,
                 input: String,
                 onValueChange: (String) -> Unit,
                 onSendNote: () -> Unit) {
    TextField(
        modifier =
            modifier
                .fillMaxWidth(),
        value = input,
        onValueChange = onValueChange,
        label = { Text("Treść notatki") },
        keyboardActions =
            KeyboardActions(
                onSend = {
                    onSendNote()
                }
            ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
    )
}