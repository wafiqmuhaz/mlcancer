package com.dicoding.asclepius.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    private lateinit var mClassifier: ImageClassifierHelper
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var mBitmap: Bitmap
    private val mInputSize = 224
    private val mModelPath = "cancer_classification.tflite"
    private val mLabelPath = "labels.txt"
    private val mSamplePath = "skin-icon.jpg"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mClassifier = ImageClassifierHelper(assets, mModelPath, mLabelPath, mInputSize)
        binding.galleryButton.setOnClickListener { startGallery() }

        resources.assets.open(mSamplePath).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true)
            binding.previewImageView.setImageBitmap(mBitmap)
        }

        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage()
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }

        binding.analyzeNow.setOnClickListener {
            analyzeImageNow()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
            try {
                val inputStream = contentResolver.openInputStream(it)
                val selectedBitmap = BitmapFactory.decodeStream(inputStream)
                mBitmap = Bitmap.createScaledBitmap(selectedBitmap, mInputSize, mInputSize, true)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    private fun analyzeImageNow() {
        if (::mBitmap.isInitialized) {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            if (results != null) {
                //Memperbarui hasil di Main
                binding.mResultTextView.text = "Hasil Analisis:${results.title}\n ------ \nConfidence: ${results.confidence}"
            } else {
                showToast("Tidak dapat mengenali gambar")
            }
        } else {
            showToast("Gambar belum dipilih")
        }
    }

    private fun analyzeImage() {
        if (::mBitmap.isInitialized) {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            if (results != null) {
                //Memperbarui hasil di Main ketika masuk ke Result
                binding.mResultTextView.text = "Hasil Analisis:${results.title}\n ------ \nConfidence: ${results.confidence}"
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
                intent.putExtra(ResultActivity.EXTRA_PREDICTION, results.title)
                intent.putExtra(ResultActivity.EXTRA_CONFIDENCE, results.confidence)
                startActivity(intent)
            } else {
                showToast("Tidak dapat mengenali gambar")
            }
        } else {
            showToast("Gambar belum dipilih")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}