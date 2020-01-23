package com.example.faceanalyserapp

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.ml.custom.*
import kotlinx.android.synthetic.main.activity_pic_object_detection.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class PicObjectDetection : AppCompatActivity() {


    private fun textToArrayList(): ArrayList<String> {

        val values = ArrayList<String>()
        try {
            val bReader = BufferedReader(InputStreamReader(assets.open("labelmap.txt")))

            var line = bReader.readLine()
            while (line != null) {
                values.add(line)
                line = bReader.readLine()
            }
            bReader.close()

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            return values
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic_object_detection)

        var arrayOfLabels: ArrayList<String> = textToArrayList()

        detectObjectBtn.setOnClickListener {
            // get all labels

            val localModel = FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("detect.tflite")
                .build()

            val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
            val interpreter = FirebaseModelInterpreter.getInstance(options)

            val inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
                .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 5))
                .build()

            val bitmap =
                Bitmap.createScaledBitmap(
                    getDrawable(R.drawable.image)!!.toBitmap(),
                    224,
                    224,
                    true
                )

            val batchNum = 0
            val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
            for (x in 0..223) {
                for (y in 0..223) {
                    val pixel = bitmap.getPixel(x, y)
                    // Normalize channel values to [-1.0, 1.0]. This requirement varies by
                    // model. For example, some models might require values to be normalized
                    // to the range [0.0, 1.0] instead.
                    input[batchNum][x][y][0] = (Color.red(pixel) - 127) / 255.0f
                    input[batchNum][x][y][1] = (Color.green(pixel) - 127) / 255.0f
                    input[batchNum][x][y][2] = (Color.blue(pixel) - 127) / 255.0f
                }
            }

            val inputs = FirebaseModelInputs.Builder()
                .add(input) // add() as many input arrays as your model requires
                .build()

            interpreter!!.run(inputs, inputOutputOptions)
                .addOnSuccessListener { result ->
                    Log.i("Success", result.getOutput(0))
                }
                .addOnFailureListener { e ->
                    Log.i("Error", e.toString())
                    throw e
                }
        }


    }
}
