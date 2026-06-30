package com.example.testdemo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieService
import com.example.testdemo.viewmodels.MoviesViewModel

@Composable
fun MovieListScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    var query by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search movies") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { viewModel.searchMovies(query) }),
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
                    Text(
                        text = refresh.error.localizedMessage ?: "Something went wrong",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    )
                else -> Unit
            }
        }
    }
}

@Composable
private fun MoviePoster(movie: Movie, onClick: () -> Unit) {
    val poster = movie.posterPath
    if (poster != null) {
        AsyncImage(
            model = MovieService.getFullImageUrl(poster),
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
