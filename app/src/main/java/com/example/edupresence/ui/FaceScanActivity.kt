package com.example.edupresence.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.edupresence.databinding.ActivityFaceScanBinding
import com.example.edupresence.viewmodel.FaceScanViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetectorOptions
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class FaceScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceScanBinding
    private val viewModel: FaceScanViewModel by viewModels()
    private lateinit var faceDetector: com.google.mlkit.vision.face.FaceDetector

    private val supabase = createSupabaseClient(
        supabaseUrl = "https://eglqipkefxbygdjixspr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ) {
        install(Storage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
        faceDetector = com.google.mlkit.vision.face.FaceDetection.getClient(options)

        startCamera { bitmap ->
            processFaceDetection(bitmap)
        }

        viewModel.locationVerified.observe(this) { verified ->
            if (!verified) {
                Toast.makeText(this, "Location not verified", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera(onFaceDetected: (Bitmap) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            val imageAnalysis = ImageAnalysis.Builder().build().also { analysis ->
                analysis.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    val bitmap = imageProxy.toBitmap()  // pastikan kamu punya ekstensi ini
                    onFaceDetected(bitmap)
                    imageProxy.close()
                }
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun processFaceDetection(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    lifecycleScope.launch {
                        val byteArray = bitmap.toByteArray() // pastikan ekstensi ada
                        val bucket = supabase.storage.from("attendance-images")
                        val fileName = "photo_${System.currentTimeMillis()}.jpg"
                        bucket.upload(fileName, byteArray) {
                            upsert = false
                        }
                        Toast.makeText(this@FaceScanActivity, "Face detected and uploaded", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "No face detected", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Face detection failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        faceDetector.close()
    }
}
