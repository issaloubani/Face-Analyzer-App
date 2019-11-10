package com.example.faceanalyserapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.faceanalyserapp.Modules.ImageConverter
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.android.synthetic.main.activity_analyzation.*
import org.eazegraph.lib.models.PieModel

class AnalyzationActivity : AppCompatActivity() {

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyzation)


        // tell some info
        Toast.makeText(applicationContext, "Swipe to see other component", Toast.LENGTH_LONG).show()

        // analyze image
        analyzeImage({}, {})

    }


    private fun loadImg(): Bitmap {
        val sp = getSharedPreferences(
            resources.getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE
        )

        val str: String? = sp.getString(resources.getString(R.string.image_bitmap_key), null)
        return ImageConverter.StringToBitMap(str!!)!!
    }

    private fun analyzeImage(
        onSuccess: (detectedFaces: List<FirebaseVisionFace>) -> Unit,
        onFailure: (exception: Exception) -> Unit
    ) {

        // retrieve image
        val currentFrameBitmap: Bitmap = loadImg()

        // build image
        val image = FirebaseVisionImage.fromBitmap(currentFrameBitmap)


        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        val detector = FirebaseVision.getInstance().getVisionFaceDetector(options)


        detector.detectInImage(image)
            .addOnSuccessListener {

                onSuccess(it)

            }
            .addOnFailureListener {

                onFailure(it)

            }
    }

    @SuppressLint("ResourceType")
    private fun initPieChart(faces: List<FirebaseVisionFace>) {
        if (faces.isNotEmpty()) {
            // add pie slice
            piechart.addPieSlice(
                PieModel(
                    "Left Eye %",
                    faces[0].leftEyeOpenProbability * 100,
                    Color.parseColor(resources.getString(R.color.lEyePieChartColor))
                )
            )
            piechart.addPieSlice(
                PieModel(
                    "Right Eye %",
                    faces[0].rightEyeOpenProbability * 100,
                    Color.parseColor(resources.getString(R.color.rEyePieChartColor))
                )
            )
            piechart.addPieSlice(
                PieModel(
                    "Smiling %",
                    faces[0].smilingProbability * 100,
                    Color.parseColor(resources.getString(R.color.smilePieChartColor))
                )
            )
        } else {
            piechart.addPieSlice(
                PieModel(
                    "No Faces Detected",
                    0f,
                    Color.parseColor(resources.getString(R.color.gray))
                )
            )
        }
    }


}
