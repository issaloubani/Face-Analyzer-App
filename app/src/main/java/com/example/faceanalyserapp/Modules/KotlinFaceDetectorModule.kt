package com.example.faceanalyserapp.Modules

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

import java.util.ArrayList

import io.fotoapparat.facedetector.Rectangle
import io.fotoapparat.preview.Frame

class KotlinFaceDetectorModule(private val currentFrame: Frame) {
    private var metadata: FirebaseVisionImageMetadata? = null

    private fun initFirebaseVisionImageMetadata(): FirebaseVisionImageMetadata {

        return FirebaseVisionImageMetadata.Builder()
            .setWidth(this.currentFrame.size.width)
            .setHeight(this.currentFrame.size.height)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
            .build()
    }

    fun detectFaces(onCompleteListener: (List<FirebaseVisionFace>) -> Unit) {
        this.metadata = initFirebaseVisionImageMetadata()
        val image = FirebaseVisionImage.fromByteArray(this.currentFrame.image, metadata!!)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)



        detector.detectInImage(image)
            .addOnSuccessListener { faces ->

                onCompleteListener(faces)
            }
            .addOnFailureListener { e ->


            }


    }

}
