package com.maryya.ingredible

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.text.Text  // Ensure this import is at the top of your file


class MainActivity : ComponentActivity() {
    // 0. Init: Initialize mutable state to hold recognized text
    private val recognizedText = mutableStateOf("No text recognized yet")

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_CAMERA_PERMISSION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 0. Init: Set content view to CameraHandler composable
        setContent {
            CameraHandler()
        }
    }


    @Composable
    fun CameraHandler() {
        // 1. Button click is processing
        Button(onClick = {
            // Check for camera permission before starting camera
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // 2. Camera is started
                startCamera()
            } else {
                // Request camera permission
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }) {
            Text("Capture and Recognize")
        }
        // 5. Text Output: Display recognized text below the button
        Text(recognizedText.value)
    }


    private fun startCamera() {
        // 2. Camera is started: Create intent to capture image
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Start activity to capture image
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 3. Photo is taken: Retrieve captured image as bitmap
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // 4. OCR is in process: Process image for text recognition
            processImage(imageBitmap)
        }
    }

    private fun processImage(bitmap: Bitmap) {
        // 4. OCR is in process: Create InputImage and initialize TextRecognition client
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        // Process image for text recognition
        recognizer.process(image)
            .addOnSuccessListener { recognizedTextResult ->
                // 5. Text Output: Update recognized text state with result
                val resultText = recognizedTextResult.text
                recognizedText.value = resultText
            }
    }

    @Composable
    fun RecognizedTextScreen() {
        var recognizedText by remember { mutableStateOf("No text recognized yet") }

        Button(onClick = {
            // Check for camera permission before starting camera
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is granted, start camera
                startCamera()
            } else {
                // Request camera permission
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
        }) {
            Greeting("Capture and Recognize 1")
        }

        Greeting(recognizedText)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)  // Call to super

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, start camera
                    startCamera()
                } else {
                    // Permission denied, show a message to the user or handle denial
                }
                return
            }
            else -> {
                // Ignore other requests
            }
        }
    }
//
//    private fun startCamera() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            makePic(imageBitmap) { text ->
//                recognizedText = text
//            }
//        }
//    }

    private fun makePic(bitmap: Bitmap, onTextRecognized: (String) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { recognizedText ->
                // Ensure recognizedText is of type Text, and not an obfuscated type
                val resultText = recognizedText.text
                for (block in recognizedText.textBlocks) {
                    val blockText = block.text
                    val blockCornerPoints = block.cornerPoints
                    val blockFrame = block.boundingBox
                    for (line in block.lines) {
                        val lineText = line.text
                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox
                        for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox
                        }
                    }
                }

            }
    }

    @Composable
    fun Greeting(name: String) {
        Text(
            text = "$name!"
        )
    }
}