package com.example.ui.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun DetailsScreen(id: Int, viewModel: MovieDetailsViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    Text(text = "I am the details screen")
}