package com.example.ui.list

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ListScreen(viewModel: MoviesListViewModel = hiltViewModel(), onMovieClick: (id: Int) -> Unit) {
    Column {
        Text(text = "I am the list screen")
        Button(onClick = { onMovieClick(1) }) {
            Text(text = "test nav to details")
        }
    }
}