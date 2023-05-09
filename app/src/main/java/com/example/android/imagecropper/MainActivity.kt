package com.example.android.imagecropper

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.canhub.cropper.CropImageView
import com.example.android.imagecropper.ui.theme.ImageCropperTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageCropperTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val viewModel: ImageCropperViewModel = hiltViewModel()

    var croppedImageUri = remember { viewModel.croppedImageUri }
    Box() {
        croppedImageUri.value?.let {
            ImagePopup(url = it.toString()) {
                viewModel.croppedImageUri.value = null
            }
        }

        Column(modifier.fillMaxSize()) {
            SecondaryTopBarWithAction(onBackClick = {
                navController.navigateUp()
            }, onButtonClick = {
                viewModel.cropImageView.croppedImageAsync()
            })

            ImageCropper(
                navController = navController,
                cropShape = CropImageView.CropShape.RECTANGLE,
                viewModel = viewModel,
                imagePath = "https://img.freepik.com/free-photo/chicken-wings-barbecue-sweetly-sour-sauce-picnic-summer-menu-tasty-food-top-view-flat-lay_2829-6471.jpg"
            )

        }
    }
}
