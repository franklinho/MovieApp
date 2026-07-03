package com.example.testdemo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.example.testdemo.models.Movie
import com.example.testdemo.networking.MovieService
import com.example.testdemo.ui.theme.TestDemoTheme
import com.example.testdemo.viewmodels.MoviesViewModel

@Composable
fun MovieListScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movies = viewModel.movies.collectAsLazyPagingItems()
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
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
                contentPadding = PaddingValues(8.dp),
            ) {
                items(count = movies.itemCount, key = movies.itemKey { it.id }) { index ->
                    movies[index]?.let { movie ->
                        MoviePoster(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            modifier = Modifier.padding(4.dp),
                        )
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
private fun MoviePoster(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .clickable(onClick = onClick),
    ) {
        val poster = movie.posterPath
        if (poster != null) {
            AsyncImage(
                model = MovieService.getFullImageUrl(poster),
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = movie.title.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoviePosterPreview() {
    TestDemoTheme {
        MoviePoster(
            movie = Movie(id = 1, title = "Preview Movie"),
            onClick = {},
        )
    }
}
