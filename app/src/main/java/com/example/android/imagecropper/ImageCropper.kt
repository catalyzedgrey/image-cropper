package com.example.android.imagecropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropper(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ImageCropperViewModel = hiltViewModel(),
    cropShape: CropImageView.CropShape,
    imagePath: String,
    aspectRatioX: Int = if (cropShape == CropImageView.CropShape.OVAL) 1 else 340,
    aspectRatioY: Int = if (cropShape == CropImageView.CropShape.OVAL) 1 else 120,
    listener: CropImageView.OnCropImageCompleteListener? = null,
) {
    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = dpToPx(configuration.screenWidthDp.toFloat(), context)
    val screenHeight = dpToPx((configuration.screenHeightDp - 90).toFloat(), context)

    LaunchedEffect(key1 = viewModel.addedImageUrl.value) {
        if (viewModel.addedImageUrl.value.isNotEmpty()) {
            navController.previousBackStackEntry?.savedStateHandle
                ?.apply {
                    set("cover_picture", viewModel.addedImageUrl.value)
                }
            navController.navigateUp()
            viewModel.addedImageUrl.value = ""
        }
    }

    LaunchedEffect(Unit) {
        // This is to load bitmap from url, there was another/better way to load files just
        // from a uri before. We can go back to that if need be
        coroutineScope.launch(Dispatchers.IO) {
            try {
                var image = if (URLUtil.isValidUrl(imagePath)) {
                    val url = URL(imagePath)
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                } else {
                    BitmapFactory.decodeFile(imagePath)
                }

                if (image.width < screenWidth) {
                    val aspectRatio: Float = image.width.toFloat() /
                            image.height.toFloat()
                    val width = screenWidth
                    var height = (width / aspectRatio).roundToInt()
                    if (height > screenHeight)
                        height = screenHeight

                    image = Bitmap.createScaledBitmap(
                        image, width, height, true
                    )
                }
                bitmap.value = image
            } catch (e: Exception) {
                Log.e("ImageCropper", e.stackTraceToString())
            }
        }
    }

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            ) {
                AsyncImage(
                    model = R.drawable.ic_rotate_cw,
                    contentDescription = "Rotate",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                        .clickable {
                            viewModel.cropImageView.rotateImage(90)
                        },
                )
            }
        }) { padding ->

        AndroidView(
            factory = { context ->
                viewModel.cropImageView = LayoutInflater.from(context)
                    .inflate(R.layout.image_cropper, null, false) as CropImageView
                viewModel.cropImageView.layoutParams =
                    ViewGroup.LayoutParams(screenWidth, screenHeight)
                viewModel.cropImageView.setOnCropImageCompleteListener(
                    listener ?: viewModel.listener
                )
                viewModel.cropImageView.setImageBitmap(bitmap.value)
                viewModel.cropImageView.setMinCropResultSize(600, 320)
                viewModel.cropImageView.setCenterMoveEnabled(true)
                viewModel.cropImageView.setAspectRatio(aspectRatioX, aspectRatioY)
                viewModel.cropImageView.cropShape = cropShape
                viewModel.cropImageView.guidelines = CropImageView.Guidelines.ON
                viewModel.cropImageView.isAutoZoomEnabled = false
                viewModel.cropImageView
            },
            modifier = modifier.padding(padding),
            update = {
                it.setImageBitmap(bitmap.value)
                Log.d("ImageCropper", "${bitmap.value}")
            }
        )
    }
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}
