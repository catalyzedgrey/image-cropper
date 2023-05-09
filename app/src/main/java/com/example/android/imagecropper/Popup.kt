package com.example.android.imagecropper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.example.android.imagecropper.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun ImagePopup(
    url: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0x323232))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bar_back),
                contentDescription = "Close Image",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Start)
                    .clickable {
                        onDismiss.invoke()
                    }
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp)
                    .weight(1f)
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Image popup",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .align(Alignment.Center)
                        .fillMaxWidth()
                )
            }
        }
    }
}