package io.github.posaydone.filmix.presentation.common.mobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.posaydone.filmix.core.model.Show

@Composable
fun ShowCard(show: Show, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            disabledContentColor = Color(android.graphics.Color.TRANSPARENT),
            containerColor = Color(android.graphics.Color.TRANSPARENT),
        ),
        onClick = { onClick() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .width(124.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .crossfade(true)
                    .data(show.poster)
                    .build(),
                contentDescription = show.title,
                modifier = Modifier
                    .aspectRatio(2f / 3f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)), // Optional: rounded corners
                contentScale = ContentScale.Crop
            )


            Text(
                style = MaterialTheme.typography.titleSmall,
                text = show.title,
                maxLines = 2,
                minLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(4.dp),
            )
        }
    }
}