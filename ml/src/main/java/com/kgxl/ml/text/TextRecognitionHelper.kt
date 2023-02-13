package com.kgxl.ml.text

import android.media.Image
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


/**
 * Created by zjy on 2023/2/9
 */
object TextRecognitionHelper {
    fun recognition(image: Image, invoke: (Text) -> Unit) {
        val image = InputImage.fromMediaImage(image, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions())
        recognizer.process(image)
            .addOnSuccessListener { texts ->
                invoke.invoke(texts)
            }
            .addOnFailureListener { e -> // Task failed with an exception
                e.printStackTrace()
            }
    }
}