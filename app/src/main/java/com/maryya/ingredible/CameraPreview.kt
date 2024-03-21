import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.maryya.ingredible.SharedViewModel
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

    val mayContainIsFound = remember { mutableStateOf(false)}

    val recognizedBoxes = remember { mutableStateOf(listOf<Rect>()) }

    var statusText by remember { mutableStateOf("OCR is deactivated") }
    val recognizedWordsSet = remember { mutableStateOf(setOf<WordTriple>()) }
    val recognizedWordsMayContainSet = remember { mutableStateOf(setOf<WordTriple>()) }

    val mayContainRegexPattern = "contenere" // This pattern matches "Puo contenere" and "Può contenere"
    val mayContainRegex = Regex(mayContainRegexPattern, setOf(RegexOption.IGNORE_CASE))

    if(!isOCRActive) {
        viewModel.resetList()
    }

    val ocrHandler = remember { OCRHandler(ContextCompat.getMainExecutor(context)) { visionText ->
        statusText = "Text recognition is started"
        var newText = visionText.text;

        val boxes = visionText.textBlocks.mapNotNull { it.boundingBox }.toList()
        recognizedBoxes.value = boxes

        val mayContainMatch = mayContainRegex.find(newText)
        val mayContainIndex = mayContainMatch?.range?.first ?: -1

        if(mayContainIndex > -1 && !mayContainIsFound.value) {
            // Iterate over recognizedWordsSet and add each word to the viewModel's itemList
            recognizedWordsSet.value.forEach { wordTriple ->
                // Assuming the word you want to add is in a property named 'word' of WordTriple
                viewModel.itemList.add(wordTriple.word) // Replace 'word' with the actual property name that holds the word
            }
            recognizedWordsSet.value = emptySet()

            // Update any necessary flags or state variables here, if needed
            mayContainIsFound.value = true // Assuming this is the correct place to update this flag
        }

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

                if (mayContainIndex == -1 || wordIndex < mayContainIndex) {
                    recognizedWordsSet.value = recognizedWordsSet.value + newTriple
                } else {
                    recognizedWordsMayContainSet.value = recognizedWordsMayContainSet.value + newTriple
                }
            }
        }

    }}

    LaunchedEffect(isOCRActive) {
        ocrHandler.isActive = isOCRActive

        if (!isOCRActive) {
            // Delay to clear the list after the eye button is released mela something
            delay(1000)
            recognizedWordsSet.value = emptySet()
            recognizedWordsMayContainSet.value = emptySet()
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
    Column(modifier = Modifier.fillMaxSize()) {
        if (recognizedWordsSet.value.isNotEmpty()) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(recognizedWordsSet.value.toList()) { wordTriple ->
                    wordTriple?.let {
                        WordPill(wordTriple, viewModel)
                    }
                }
            }
        }

        if (recognizedWordsMayContainSet.value.isNotEmpty()) {
            // Apply conditional padding or layout changes here if needed
            Text("After 'Puo contenere:'", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp).background(Color.White))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(recognizedWordsMayContainSet.value.toList()) { wordTriple ->
                    wordTriple?.let {
                        WordPill(wordTriple, viewModel)
                    }
                }
            }
        }
    }
    @Composable
    fun DrawOverlay(modifier: Modifier = Modifier, boxes: List<Rect>) {
        Canvas(modifier = modifier) {
            boxes.forEach { box ->
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(box.left.toFloat(), box.top.toFloat()),
                    size = Size(box.width().toFloat(), box.height().toFloat()),
                    style = Stroke(width = 5f)
                )
            }
        }
    }

//    DrawOverlay(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(top = 32.dp), // Adjust padding as needed
//        boxes = recognizedBoxes.value
//    )

    StatusVm(statusText)
}
