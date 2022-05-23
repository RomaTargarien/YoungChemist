package com.chemist.youngchemist.ui.screen.main.qr.analyzer

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class BaseImageAnalyzer<T> : ImageAnalysis.Analyzer {


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            detectInImage(InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees))
                .addOnSuccessListener { results ->
                    onSuccess(results)
                    image.close()
                }.addOnFailureListener {
                    onFailure(it)
                    image.close()
                }
        }


    }

    abstract fun detectInImage(image: InputImage): Task<T>

    abstract fun stop()

    abstract fun onSuccess(results: T)

    abstract fun onFailure(e: Exception)

}