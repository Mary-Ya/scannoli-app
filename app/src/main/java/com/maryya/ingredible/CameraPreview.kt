import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.*

@Composable

fun CameraPreview(modifier: Modifier, isOCRActive: Boolean, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var recognizedWords by remember { mutableStateOf("") }

    val ocrHandler = remember { OCRHandler(ContextCompat.getMainExecutor(context)) { words ->
        val matches = words.filter { viewModel.itemList.contains(it.lowercase()) }
        if (matches.isNotEmpty()) {
            recognizedWords = matches
        }
    } }

    LaunchedEffect(isOCRActive) {
        ocrHandler.isActive = isOCRActive
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
    if (recognizedWords.isNotEmpty()) {
        Text(text = recognizedWords)
        // Reset logic after a delay, if needed
    }
}