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
                    // can be just Paint()


                    overlayDrawerImageView.setImageBitmap(bitmap)
                }

                .addOnFailureListener {


                }
        }
    }
}
