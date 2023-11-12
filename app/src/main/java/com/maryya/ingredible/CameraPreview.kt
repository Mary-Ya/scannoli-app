import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CameraPreview(modifier: Modifier, isOCRActive: Boolean, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var recognizedWordsSet by remember { mutableStateOf(setOf<String>()) }
    var displayWords by remember { mutableStateOf("") }

    val ocrHandler = remember { OCRHandler(ContextCompat.getMainExecutor(context)) { recognizedText ->
        val matches = viewModel.itemList.filter { item ->
            recognizedText.lowercase().contains(item.lowercase())
        }.toSet()

        if (matches.isNotEmpty()) {
            recognizedWordsSet = recognizedWordsSet.union(matches)
            displayWords = recognizedWordsSet.joinToString(", ")
        }
    }}

    LaunchedEffect(isOCRActive) {
        ocrHandler.isActive = isOCRActive
        if (!isOCRActive) {
            // Delay to clear the list after the eye button is released
            delay(1000)
            recognizedWordsSet = emptySet()
            displayWords = ""
        }
    }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .build()
            .also { analysisUseCase ->
                analysisUseCase.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    ocrHandler.processImageProxy(imageProxy)
                }
            }
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis) // Include imageAnalysis
                    } catch (exc: Exception) {
                        // Handle any errors
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )
    } else {
        // Handle missing permission
    }

    // Optionally display recognized words
    if (displayWords.isNotEmpty()) {
        Text(text = displayWords,
            modifier = Modifier
                .padding(16.dp)  // Add some padding
                .background(Color.Black.copy(alpha = 0.7f))  // Semi-transparent black background
                .padding(8.dp),  // Padding inside the background
            color = Color.White,
            fontSize = 20.sp,  // Larger font size
            fontWeight = FontWeight.Bold, // Bold font weight
     )
        // Reset logic after a delay, if needed
    }
}