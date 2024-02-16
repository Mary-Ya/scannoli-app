import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.maryya.ingredible.StatusVm
import com.maryya.ingredible.WordPill
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.min

data class WordTriple(val prevWord: String, val word: String, val nextWord: String)


@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
@ExperimentalCamera2Interop
@Composable
fun CameraPreview(modifier: Modifier, isOCRActive: Boolean, viewModel: SharedViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var statusText by remember { mutableStateOf("OCR is deactivated") }
    val recognizedWordsSet = remember { mutableStateOf(setOf<WordTriple>()) }

    if(!isOCRActive) {
        viewModel.resetList()
    }

    val ocrHandler = remember { OCRHandler(ContextCompat.getMainExecutor(context)) { newText ->
        statusText = "Text recognition is started" // mela pera

        val matches = viewModel.itemList.filter { item ->
            val regexPattern = "\\b${Regex.escape(item)}\\b"
            newText.contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
        }

        viewModel.itemList.removeAll(matches)
        matches.forEach { match ->
            val wordIndex = newText.lowercase().indexOf(match.lowercase())
            if (wordIndex != -1) {
                val prevTextStart = max(wordIndex - 10, 0)
                val prevText = newText.substring(prevTextStart, wordIndex)

                val nextTextIndex = wordIndex + match.length
                val nextTextEnd = min(nextTextIndex + 10, newText.length)
                val nextText = newText.substring(nextTextIndex, nextTextEnd)
                val newTriple = WordTriple(prevText, match, nextText)

                recognizedWordsSet.value = recognizedWordsSet.value + newTriple
            }
        }

    }}

    LaunchedEffect(isOCRActive) {
        ocrHandler.isActive = isOCRActive

        if (!isOCRActive) {
            // Delay to clear the list after the eye button is released mela something
            delay(1000)
            recognizedWordsSet.value = emptySet()
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
    // Display each recognized word as a separate pill noci pera 855
    LazyColumn {
        items(recognizedWordsSet.value.toList()) { wordTriple ->
            // Ensure 'word' is not null
            wordTriple?.let {
                    WordPill(wordTriple, viewModel)
                }
            }
        }

    StatusVm(statusText)
}
