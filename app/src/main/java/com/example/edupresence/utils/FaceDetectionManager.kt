package com.example.edupresence.utils

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FaceDetectionManager(
    private val context: Context,
    private val previewView: PreviewView,
    private val onStatusUpdate: (String) -> Unit
) {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector = FaceDetection.getClient(options)

    fun startCamera(onFaceDetected: (android.graphics.Bitmap) -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    detector.process(image)
                        .addOnSuccessListener { faces ->
                            if (faces.isNotEmpty()) {
                                val face = faces[0]
                                if (face.leftEyeOpenProbability != null && face.rightEyeOpenProbability != null) {
                                    if (face.leftEyeOpenProbability!! > 0.5 && face.rightEyeOpenProbability!! > 0.5) {
                                        onStatusUpdate("Face detected, eyes open")
                                        val bitmap = android.graphics.Bitmap.createBitmap(
                                            mediaImage.width, mediaImage.height, android.graphics.Bitmap.Config.ARGB_8888
                                        )
                                        onFaceDetected(bitmap)
                                    } else {
                                        onStatusUpdate("Please open both eyes")
                                    }
                                }
                            } else {
                                onStatusUpdate("No face detected")
                            }
                            imageProxy.close()
                        }
                        .addOnFailureListener {
                            onStatusUpdate("Detection failed")
                            imageProxy.close()
                        }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                onStatusUpdate("Camera error: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun stopCamera() {
        cameraExecutor.shutdown()
        detector.close()
    }
}