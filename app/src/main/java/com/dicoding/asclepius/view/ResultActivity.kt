package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val prediction = intent.getStringExtra(EXTRA_PREDICTION)
        val confidence = intent.getFloatExtra(EXTRA_CONFIDENCE, 0f)

        imageUri?.let {
            val uri = Uri.parse(it)
            binding.resultImage.setImageURI(uri)
        }

        val resultText = "Hasil Analisis: $prediction\nConfidence: $confidence"
        binding.resultText.text = resultText

    }


    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PREDICTION = "extra_prediction_result"
        const val EXTRA_CONFIDENCE = "extra_confidence_result"
    }
}