package com.example.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coreui.components.AppScaffold
import com.example.coreui.components.RatingBar
import com.example.domain.models.Movie
import com.example.ui.R
import com.example.ui.details.actions.MovieDetailsAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(id: Int, viewModel: MovieDetailsViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val detailsWrapper by viewModel.details.collectAsStateWithLifecycle()

    val movie by remember {
        derivedStateOf { detailsWrapper?.movie }
    }

    LaunchedEffect(true) {
        viewModel.enqueueAction(MovieDetailsAction.GetMovieDetails(id))
    }
    val stateDelegate = viewModel.screenStateDelegate
    stateDelegate.StatesContainer {
        AppScaffold(
            apiError = apiError,
            isLoading = loadingShown,
            scrollBehavior = scrollBehavior,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = movie?.originalTitle ?: "")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "BACK_ICON"
                            )
                        }
                    }
                )
            }) {
            movie?.let { MovieDetailsBackdrop(movie = it) }
        }
    }
}

@Composable
fun MovieDetailsBackdrop(movie: Movie) {
    Box(contentAlignment = Alignment.TopCenter) {
        MovieImage(modifier = Modifier.align(Alignment.TopCenter), url = movie.posterUrl)
        ConstraintLayout(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
                .background(Color.Gray)
                .padding(8.dp)
        ) {

            val (title, overview, rate, releaseDate) = createRefs()
            Text(text = movie.originalTitle.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    })
            Text(text = movie.overview.toString(), modifier = Modifier
                .padding(top = 4.dp)
                .constrainAs(overview) {
                    start.linkTo(parent.start)
                    top.linkTo(title.bottom)
                })
            Text(text = stringResource(R.string.released_at, movie.releaseDate.toString()),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .constrainAs(releaseDate) {
                        start.linkTo(parent.start)
                        top.linkTo(overview.bottom)
                    })
            RatingBar(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(top = 4.dp)
                    .constrainAs(rate) {
                        start.linkTo(parent.start)
                        top.linkTo(releaseDate.bottom)
                    }, rating = movie.rate ?: 0F,
                spaceBetween = 4.dp
            )
        }
    }
}

@Composable
fun MovieImage(modifier: Modifier = Modifier, url: String?) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
        model = ImageRequest.Builder(LocalContext.current).data(url).placeholder(com.example.core.R.drawable.placeholder)
            .build(),
        contentDescription = "MOVIE_BACKDROP"
    )
}