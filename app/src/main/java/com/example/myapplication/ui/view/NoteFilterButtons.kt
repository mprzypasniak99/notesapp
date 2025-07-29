package com.example.myapplication.ui.view

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.NoteFilter

@Composable
fun NoteFilterButtons(modifier: Modifier = Modifier,
                      selectedFilter: NoteFilter,
                      onClick: (NoteFilter) -> Unit) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        NoteFilter.entries.forEach {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = it.ordinal,
                    count = NoteFilter.entries.size
                ),
                onClick = { onClick(it) },
                selected = selectedFilter == it,
                label = { Text(it.label) }
            )
        }
    }
}