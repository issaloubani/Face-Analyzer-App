package com.example.faceanalyserapp.Modules

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.widget.ImageView
import com.example.faceanalyserapp.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor

/**
 *  Created by ISSA LOUBANI 15/11/2019
 */

data class AwesomeFrameProcessor(
    val context: Context,
    val cameraView: CameraView?,
    val cameraOverlayImageView: ImageView?
) : FrameProcessor {

    internal var facialTracking: Boolean = false
    internal var dotPaintColor = Color.RED
    internal var linePaintColor = Color.GREEN
    internal var dotStroke = 4F
    internal var lineStroke = 2F


    // firebase attributes
    private var metadata: FirebaseVisionImageMetadata? = null
    private var firebaseVisionImage: FirebaseVisionImage? = null
    private var options: FirebaseVisionFaceDetectorOptions? = null
    private var faceDetector: FirebaseVisionFaceDetector? = null


    private fun analyzeFrame(
        frame: Frame, facing: Facing,
        onSuccess: (List<FirebaseVisionFace>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val width = frame.size.width
        val height = frame.size.height

        this.metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(width)
            .setHeight(height)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(if (facing == Facing.FRONT) FirebaseVisionImageMetadata.ROTATION_270 else FirebaseVisionImageMetadata.ROTATION_90)
            .build()

        this.firebaseVisionImage = FirebaseVisionImage.fromByteArray(frame.data, metadata!!)
        this.options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
        this.faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options!!)

        this.faceDetector!!.detectInImage(firebaseVisionImage!!)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

    @SuppressLint("WrongThread")
    override fun process(frame: Frame) {

        val width = frame.size.width
        val height = frame.size.height

        analyzeFrame(frame, cameraView!!.facing, {

            // settings
            val bitmap = Bitmap.createBitmap(
                height,
                width,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            val dotPaint = Paint()
            dotPaint.color = dotPaintColor
            dotPaint.style = Paint.Style.FILL
            dotPaint.strokeWidth = dotStroke
            val linePaint = Paint()
            linePaint.color = linePaintColor
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = lineStroke

            // draw on overlay
            if (facialTracking) { // draw facial details
                for (face in it) {

                    val faceContours =
                        face.getContour(FirebaseVisionFaceContour.FACE).points
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
                        face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP)
                            .points
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
                        face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM)
                            .points
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
                        face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP)
                            .points
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
                        face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM)
                            .points
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
                        face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM)
                            .points
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
                        face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM)
                            .points
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
                }
            }

            it.forEach { face ->


                val d = context.resources.getDrawable(R.drawable.ic_rectangle, null)
                d.setBounds(
                    face.boundingBox.left,
                    face.boundingBox.top,
                    face.boundingBox.right,
                    face.boundingBox.bottom
                )
                d.draw(canvas)
            }

            if (cameraView.facing == Facing.FRONT) {
                val matrix = Matrix()
                matrix.preScale(-1F, 1F)
                val flippedBitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
                cameraOverlayImageView!!.setImageBitmap(flippedBitmap)
            } else {
                cameraOverlayImageView!!.setImageBitmap(bitmap)
            }

        },
            {
                cameraOverlayImageView!!.setImageBitmap(null)
            })
    }
}