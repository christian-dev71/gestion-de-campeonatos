package com.example.gestiondecampeonatos.ui.components

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gestiondecampeonatos.R
import com.example.gestiondecampeonatos.data.local.image.decodeTeamImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun TeamAvatar(
    imageUri: String?,
    modifier: Modifier = Modifier,
    @DrawableRes placeholderRes: Int = R.drawable.ic_teams,
) {
    val context = LocalContext.current.applicationContext
    val image by produceState<ImageBitmap?>(null, imageUri) {
        value = null
        value = withContext(Dispatchers.IO) {
            imageUri?.let {
                try {
                    decodeTeamImage(context, Uri.parse(it), 256).asImageBitmap()
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
    Box(
        modifier = modifier.size(48.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        val bitmap = image
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        } else {
            Icon(
                painter = painterResource(placeholderRes),
                contentDescription = null,
                modifier = Modifier.padding(10.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
