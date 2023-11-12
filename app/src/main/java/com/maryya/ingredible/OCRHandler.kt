import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRHandler {

    fun processImage(bitmap: Bitmap, onTextRecognized: (String) -> Unit) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { recognizedText ->
                onTextRecognized(recognizedText.text)
            }
            .addOnFailureListener {
                // Handle any errors
            }
    }
}