import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.util.Size
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
@OptIn(ExperimentalCamera2Interop::class)
@ExperimentalCamera2Interop
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
                        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
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
    // Display each recognized word as a separate pill
    LazyColumn {
        items(recognizedWordsSet.toList()) { word ->
            val index = recognizedWordsSet.toList().indexOf(word)
            val prevWord = recognizedWordsSet.elementAtOrNull(index - 1) ?: ""
            val nextWord = recognizedWordsSet.elementAtOrNull(index + 1) ?: ""
            WordPill(word, prevWord, nextWord, viewModel)
        }
    }

}
@Composable
fun WordPill(currentWord: String, prevWord: String, nextWord: String, viewModel: SharedViewModel) {
    val color = viewModel.colorList.random().copy(alpha = 0.7f)

    // Display previous, current, and next words
    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = prevWord,
            modifier = Modifier.background(color.copy(alpha = 0.3f), RoundedCornerShape(50)),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = currentWord,
            modifier = Modifier.background(color, RoundedCornerShape(50)),
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = nextWord,
            modifier = Modifier.background(color.copy(alpha = 0.3f), RoundedCornerShape(50)),
            color = Color.White
        )
    }
}