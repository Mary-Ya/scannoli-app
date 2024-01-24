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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maryya.ingredible.WordPill
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

data class WordTriple(val prevWord: String, val word: String, val nextWord: String)


@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
@OptIn(ExperimentalCamera2Interop::class)
@ExperimentalCamera2Interop
@Composable
fun CameraPreview(modifier: Modifier, isOCRActive: Boolean, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var statusText by remember { mutableStateOf("OCR is deactivated") }
    val recognizedWordsSet = remember { mutableSetOf<WordTriple>() }

    val ocrHandler = remember { OCRHandler(ContextCompat.getMainExecutor(context)) { newText ->
        statusText = "Text recognition is started"

        val matches = viewModel.itemList.filter { item ->
            newText.lowercase().contains(item.lowercase())
        }

        matches.forEach { match ->
            val wordIndex = newText.lowercase().indexOf(match.lowercase())
            if (wordIndex != -1) {
                val prevTextStart = max(wordIndex - 10, 0)
                val prevText = newText.substring(prevTextStart, wordIndex)

                val nextTextIndex = wordIndex + match.length
                val nextTextEnd = min(nextTextIndex + 10, newText.length)
                val nextText = newText.substring(nextTextIndex, nextTextEnd)

                recognizedWordsSet.add(WordTriple(prevText, match, nextText))
            }
        }

    }}

    LaunchedEffect(isOCRActive) {
        ocrHandler.isActive = isOCRActive

        if (!isOCRActive) {
            // Delay to clear the list after the eye button is released
            delay(1000)
            recognizedWordsSet.clear()
            statusText = "OCR is not active"
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
        items(recognizedWordsSet.toList()) { wordTriple ->
            // Ensure 'word' is not null
            wordTriple?.let {
                    WordPill(wordTriple, viewModel)
                }
            }
        }


    // Status text Composable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Add padding to ensure the text is not right at the edge
        contentAlignment = Alignment.BottomCenter // Aligns the content to the bottom center mela u
    ) {
        Text(
            text = statusText,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
