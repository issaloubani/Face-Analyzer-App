package com.example.faceanalyserapp.Modules

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.ImageView
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 *  Created by ISSA LOUBANI 15/11/2019
 */

data class TensorflowProcessor(
    val context: Context,
    val cameraView: CameraView?,
    val cameraOverlayImageView: ImageView?
) : FrameProcessor {


    internal var linePaintColor = Color.GREEN
    internal var lineStroke = 2F

    // tensorflow settings
    private var arrayOfLabels: ArrayList<String>? = textToArrayList()
    private var inputOutputOptions: FirebaseModelInputOutputOptions? =
        FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(
                0,
                FirebaseModelDataType.FLOAT32,
                intArrayOf(1, 224, 224, 3)
            )
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 5))
            .build()
    
    private var labelInterpreter: FirebaseModelInterpreter? = null
    private var firebaseModelInputs: FirebaseModelInputs? = null

    private fun textToArrayList(): ArrayList<String> {

        val values = ArrayList<String>()
        try {
            val bReader = BufferedReader(InputStreamReader(context.assets.open("labelmap.txt")))

            var line = bReader.readLine()
            while (line != null) {
                values.add(line)
                line = bReader.readLine()
            }
            bReader.close()
            for (v in values)
                Log.i("Array is ", v)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            return values
        }
    }

    private fun getHighestConfidence(arrayList: ArrayList<Double>): Label {

        val max = arrayList.maxBy {
            it < 1 && it < arrayList.max()!!
        }

        return Label("", max!!, arrayList.indexOf(max))

    }

    /** Initialize a local model interpreter from assets file */
    private fun createLocalModelInterpreter(): FirebaseModelInterpreter {

        // Create an interpreter with the local model asset
        val localModel =
            FirebaseCustomLocalModel.Builder().setAssetFilePath("detect.tflite").build()
        val localInterpreter = FirebaseModelInterpreter.getInstance(
            FirebaseModelInterpreterOptions.Builder(localModel).build()
        )!!
        Log.d("Interpreter Status", "Local model interpreter initialized")

        // Return the interpreter
        return localInterpreter
    }

    // firebase attributes
    private var metadata: FirebaseVisionImageMetadata? = null
    private var firebaseVisionImage: FirebaseVisionImage? = null
    private var options: FirebaseVisionObjectDetectorOptions? = null
    private var faceDetector: FirebaseVisionObjectDetector? = null

    // set tensorflow settings

    private fun analyzeFrame(
        frame: Frame, facing: Facing,
        onSuccess: (List<FirebaseVisionObject>) -> Unit,
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
        this.options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()  // Optional
            .build()

        this.faceDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options!!)

        this.faceDetector!!.processImage(firebaseVisionImage!!)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)

        //    this.arrayOfLabels = textToArrayList()
//        this.inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
//            .setInputFormat(
//                0,
//                FirebaseModelDataType.FLOAT32,
//                intArrayOf(1, 224, 224, 3)
//            )
//            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 5))
//            .build()

        this.labelInterpreter = createLocalModelInterpreter()
        this.firebaseModelInputs = FirebaseModelInputs.Builder()
            .add(frame.data)
            .build()
    }

    @SuppressLint("WrongThread")
    override fun process(frame: Frame) {

        val width = frame.size.width
        val height = frame.size.height
        val frameByteArray = frame.data


        analyzeFrame(frame, cameraView!!.facing, {

            // settings
            val bitmap = Bitmap.createBitmap(
                height,
                width,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            val linePaint = Paint()
            linePaint.color = linePaintColor
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = lineStroke

            // draw on overlay


            it.forEach { detectedObject ->


                canvas.drawRect(
                    detectedObject.boundingBox.left.toFloat(),
                    detectedObject.boundingBox.top.toFloat(),
                    detectedObject.boundingBox.right.toFloat(),
                    detectedObject.boundingBox.bottom.toFloat(), linePaint
                )


            }

            this.labelInterpreter!!.run(this.firebaseModelInputs!!, this.inputOutputOptions!!)
                .addOnSuccessListener {

                    // draw text

                    val results = it.getOutput<ArrayList<Double>>(0)
                    val label = getHighestConfidence(results)

                    label.name = this.arrayOfLabels!![label.index]
                    Log.i("Detected Object : ", label.name)
                    linePaint.textSize = 32F
                    canvas.drawText(
                        label.name,
                        100F,
                        100F,
                        linePaint
                    )

                }
                .addOnFailureListener {

                }


            // set overlay
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