package com.example.faceanalyserapp

import android.annotation.SuppressLint
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Grid
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import kotlinx.android.synthetic.main.activity_natario.*
import kotlinx.android.synthetic.main.buttons_layout.*


class NatarioActivity : AppCompatActivity(), FrameProcessor {


    override fun process(frame: Frame) {
        val width = frame.size.width
        val height = frame.size.height

        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(width)
            .setHeight(height)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(if (natarioCameraView!!.facing == Facing.FRONT) FirebaseVisionImageMetadata.ROTATION_270 else FirebaseVisionImageMetadata.ROTATION_90)
            .build()

        val firebaseVisionImage = FirebaseVisionImage.fromByteArray(frame.data, metadata)
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .build()
        val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        faceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener {
                natarioCameraImageView!!.setImageBitmap(null)

                val bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val dotPaint = Paint()
                dotPaint.color = Color.RED
                dotPaint.style = Paint.Style.FILL
                dotPaint.strokeWidth = 6F
                val linePaint = Paint()
                linePaint.color = Color.RED
                linePaint.style = Paint.Style.STROKE
                linePaint.strokeWidth = 4F

                it.forEach { face ->

                    val d = resources.getDrawable(R.drawable.ic_rectangle, null)
                    d.setBounds(
                        face.boundingBox.left,
                        face.boundingBox.top,
                        face.boundingBox.right,
                        face.boundingBox.bottom
                    )
                    d.draw(canvas)
                }

                if (natarioCameraView!!.facing == Facing.FRONT) {
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


                    natarioCameraImageView!!.setImageBitmap(flippedBitmap)
                } else {
                    natarioCameraImageView!!.setImageBitmap(bitmap)
                }
            }

    }


    @SuppressLint("WrongThread")

    override fun onResume() {
        super.onResume()
        natarioCameraView.open()
    }

    override fun onPause() {
        super.onPause()
        natarioCameraView.close()
        natarioCameraView.clearFrameProcessors()
    }

    override fun onDestroy() {
        super.onDestroy()
        natarioCameraView.destroy()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_natario)


        natarioCameraView.setLifecycleOwner(this)


        natarioCameraView.addFrameProcessor(
            this
        )

        // buttons events handler
        rotateBtn.setOnClickListener {
            natarioCameraView.facing =
                if (natarioCameraView.facing == Facing.FRONT) Facing.BACK else Facing.FRONT
        }

        gridBtn.setOnClickListener {
            natarioCameraView.grid =
                if (natarioCameraView.grid == Grid.OFF) Grid.DRAW_3X3 else Grid.OFF
        }

        flashBtn.setOnClickListener {

            natarioCameraView.flash =
                if (natarioCameraView.flash == Flash.ON) Flash.OFF else Flash.ON
        }

    }
}
