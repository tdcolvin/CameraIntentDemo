package com.tdcolvin.cameraintentdemo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.tdcolvin.cameraintentdemo.ui.theme.CameraIntentDemoTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CameraIntentDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TakePhotoScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun TakePhotoScreen(modifier: Modifier = Modifier) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    Column(modifier = modifier) {
        TakePhotoButton(
            onPhotoTaken = { imageUri = it }
        )

        if (imageUri != null) {
            AsyncImage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit,
                model = imageUri,
                contentDescription = "Photo taken by user"
            )
        }
    }
}

@Composable
fun TakePhotoButton(modifier: Modifier = Modifier, onPhotoTaken: (Uri) -> Unit) {
    val context = LocalContext.current
    val pictureUri = remember { context.getTmpFileUri() }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            onPhotoTaken(pictureUri)
        }
    }

    Button(modifier = modifier, onClick = {
        launcher.launch(pictureUri)
    }) {
        Text("Take Photo")
    }
}

private fun Context.getTmpFileUri(): Uri {
    val tmpFile = File.createTempFile("photo", ".jpg", cacheDir).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(applicationContext, "${BuildConfig.APPLICATION_ID}.fileprovider", tmpFile)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CameraIntentDemoTheme {
    }
}