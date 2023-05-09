package com.example.android.imagecropper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.R
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageView
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ImageCropperViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {
    lateinit var cropImageView: CropImageView
    val addedImageUrl = mutableStateOf("")

    var imageFilePath: String = ""
    var resultImageUri: MutableState<Uri?> = mutableStateOf(null)
    var croppedImageUri: MutableState<Uri?> = mutableStateOf(null)


    val listener = CropImageView.OnCropImageCompleteListener { view, result ->
        if (result.isSuccessful) {
            // use the cropped image
            resultImageUri.value = result.uriContent
            imageFilePath =
                result.getUriFilePath(context = context, uniqueName = true).toString()

            if (resultImageUri.value?.scheme?.isNotEmpty() == true) {
                // replace Scheme to file
                val builder = Uri.Builder()
                builder.scheme("file")
                    .appendPath(imageFilePath)
                croppedImageUri.value = builder.build()
            }
        } else {
            // an error occurred cropping
            val exception = result.error
            exception?.stackTraceToString()?.let { Log.e("ImageCropperViewModel", it) }
        }
    }

}