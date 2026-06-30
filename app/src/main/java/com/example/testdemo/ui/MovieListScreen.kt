package com.example.testdemo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.ImageUrls
import com.example.testdemo.viewmodels.MoviesViewModel

@Composable
fun MovieListScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::updateSearchQuery,
            label = { Text("Search movies") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(count = movies.itemCount, key = movies.itemKey { it.id }) { index ->
                    movies[index]?.let { movie ->
                        MoviePoster(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
            }

            when (val refresh = movies.loadState.refresh) {
                is LoadState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is LoadState.Error ->
                    StatusMessage(
                        message = refresh.error.localizedMessage ?: "Something went wrong",
                        actionLabel = "Retry",
                        onAction = { movies.retry() },
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    )
                is LoadState.NotLoading ->
                    if (movies.itemCount == 0) {
                        StatusMessage(
                            message = if (query.isBlank()) {
                                "No movies available"
                            } else {
                                "No movies found"
                            },
                            actionLabel = if (query.isBlank()) null else "Clear",
                            onAction = if (query.isBlank()) {
                                null
                            } else {
                                {
                                    viewModel.updateSearchQuery("")
                                }
                            },
                            modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        )
                    }
            }
        }
    }
}

@Composable
private fun StatusMessage(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction, modifier = Modifier.padding(top = 8.dp)) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun MoviePoster(movie: Movie, onClick: () -> Unit) {
    val poster = movie.posterPath
    if (poster != null) {
        AsyncImage(
            model = ImageUrls.fullImageUrl(poster),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.aspectRatio(0.66f).clickable(onClick = onClick),
        )
    } else {
        Box(modifier = Modifier.aspectRatio(0.66f).clickable(onClick = onClick).padding(8.dp)) {
            Text(text = movie.title.orEmpty(), style = MaterialTheme.typography.bodyMedium)
        }
    }
}
