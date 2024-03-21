import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executor

class OCRHandler(private val executor: Executor, private val onTextRecognized: (Text) -> Unit) {
    private val recognizer: TextRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private var lastAnalyzedTimestamp = 0L
    private val analyzerDelay = 500L // Delay in milliseconds (0.5 seconds)

    // Function to process the image for OCR
    var isActive: Boolean = false  // Add this line

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(imageProxy: ImageProxy) {
        Log.d("OCRHandler", "Processing image, isActive: $isActive")

        val currentTimestamp = System.currentTimeMillis()
        if (!isActive || (currentTimestamp - lastAnalyzedTimestamp) < analyzerDelay) {
            imageProxy.close()
            return
        }

        lastAnalyzedTimestamp = currentTimestamp

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    onTextRecognized(visionText)
                }
                .addOnFailureListener {
                    // Handle any errors
                }
                .addOnCompleteListener {
                    imageProxy.close()  // Always close the imageProxy
                }
        } else {
            imageProxy.close()  // Close the imageProxy if mediaImage is null
        }
    }

    // Function to provide an analyzer for ImageAnalysis
    fun getAnalyzer(): ImageAnalysis.Analyzer {
        return ImageAnalysis.Analyzer { imageProxy ->
            processImageProxy(imageProxy)
        }
    }
}