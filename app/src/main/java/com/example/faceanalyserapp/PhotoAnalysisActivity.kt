package com.example.faceanalyserapp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_photo_analysis.*

class PhotoAnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_analysis)


        analyseBtn.setOnClickListener {
            Toast.makeText(applicationContext, "Analyzing !", Toast.LENGTH_SHORT).show()
            // retrieve image
            val currentFrameBitmap: Bitmap = photoImageView.drawToBitmap()

            // build image
            val image = FirebaseVisionImage.fromBitmap(currentFrameBitmap)


            val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .build()

            val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)


            detector.detectInImage(image)

                .addOnSuccessListener {
                    overlayDrawerImageView.setImageBitmap(null)

                    val bitmap = Bitmap.createBitmap(
                        overlayDrawerImageView.height,
                        overlayDrawerImageView.width,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    val dotPaint = Paint()
                    dotPaint.color = Color.RED
                    dotPaint.style = Paint.Style.FILL
                    dotPaint.strokeWidth = 6F
                    val linePaint = Paint()
                    linePaint.color = Color.GREEN
                    linePaint.style = Paint.Style.STROKE
                    linePaint.strokeWidth = 4F

                    for (face in it) {

                        val faceContours = face.getContour(FirebaseVisionFaceContour.FACE).points
                        for ((i, contour) in faceContours.withIndex()) {
                            if (i != faceContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    faceContours[i + 1].x,
                                    faceContours[i + 1].y,
                                    linePaint
                                )
                            else
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    faceContours[0].x,
                                    faceContours[0].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val leftEyebrowTopContours =
                            face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points
                        for ((i, contour) in leftEyebrowTopContours.withIndex()) {
                            if (i != leftEyebrowTopContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    leftEyebrowTopContours[i + 1].x,
                                    leftEyebrowTopContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val leftEyebrowBottomContours =
                            face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points
                        for ((i, contour) in leftEyebrowBottomContours.withIndex()) {
                            if (i != leftEyebrowBottomContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    leftEyebrowBottomContours[i + 1].x,
                                    leftEyebrowBottomContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val rightEyebrowTopContours =
                            face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points
                        for ((i, contour) in rightEyebrowTopContours.withIndex()) {
                            if (i != rightEyebrowTopContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    rightEyebrowTopContours[i + 1].x,
                                    rightEyebrowTopContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val rightEyebrowBottomContours =
                            face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points
                        for ((i, contour) in rightEyebrowBottomContours.withIndex()) {
                            if (i != rightEyebrowBottomContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    rightEyebrowBottomContours[i + 1].x,
                                    rightEyebrowBottomContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val leftEyeContours =
                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).points
                        for ((i, contour) in leftEyeContours.withIndex()) {
                            if (i != leftEyeContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    leftEyeContours[i + 1].x,
                                    leftEyeContours[i + 1].y,
                                    linePaint
                                )
                            else
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    leftEyeContours[0].x,
                                    leftEyeContours[0].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val rightEyeContours =
                            face.getContour(FirebaseVisionFaceContour.RIGHT_EYE).points
                        for ((i, contour) in rightEyeContours.withIndex()) {
                            if (i != rightEyeContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    rightEyeContours[i + 1].x,
                                    rightEyeContours[i + 1].y,
                                    linePaint
                                )
                            else
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    rightEyeContours[0].x,
                                    rightEyeContours[0].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val upperLipTopContours =
                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP).points
                        for ((i, contour) in upperLipTopContours.withIndex()) {
                            if (i != upperLipTopContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    upperLipTopContours[i + 1].x,
                                    upperLipTopContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val upperLipBottomContours =
                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).points
                        for ((i, contour) in upperLipBottomContours.withIndex()) {
                            if (i != upperLipBottomContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    upperLipBottomContours[i + 1].x,
                                    upperLipBottomContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val lowerLipTopContours =
                            face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP).points
                        for ((i, contour) in lowerLipTopContours.withIndex()) {
                            if (i != lowerLipTopContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    lowerLipTopContours[i + 1].x,
                                    lowerLipTopContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val lowerLipBottomContours =
                            face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM).points
                        for ((i, contour) in lowerLipBottomContours.withIndex()) {
                            if (i != lowerLipBottomContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    lowerLipBottomContours[i + 1].x,
                                    lowerLipBottomContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val noseBridgeContours =
                            face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE).points
                        for ((i, contour) in noseBridgeContours.withIndex()) {
                            if (i != noseBridgeContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    noseBridgeContours[i + 1].x,
                                    noseBridgeContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }

                        val noseBottomContours =
                            face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM).points
                        for ((i, contour) in noseBottomContours.withIndex()) {
                            if (i != noseBottomContours.lastIndex)
                                canvas.drawLine(
                                    contour.x,
                                    contour.y,
                                    noseBottomContours[i + 1].x,
                                    noseBottomContours[i + 1].y,
                                    linePaint
                                )
                            canvas.drawCircle(contour.x, contour.y, 4F, dotPaint)
                        }




                        it.forEach { face ->
                            canvas.drawRect(face.boundingBox, linePaint)
                        }

                        overlayDrawerImageView.setImageBitmap(bitmap)
                    }
                }
                .addOnFailureListener {


                }
        }
    }
}