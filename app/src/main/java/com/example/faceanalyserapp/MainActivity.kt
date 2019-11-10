package com.example.faceanalyserapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.faceanalyserapp.Modules.ImageConverter
import com.example.faceanalyserapp.Modules.Permissions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.facedetector.FaceDetector
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var fotoapparatInstance: Fotoapparat? = null
    private var isFront = true // by default camera in front

    override fun onStart() {
        super.onStart()
        fotoapparatInstance!!.start()
    }

    override fun onStop() {
        super.onStop()
        fotoapparatInstance!!.stop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Permissions.hasNoPermissions(applicationContext)) {
            Permissions.requestPermission(this)
        }

        fotoapparatInstance = Fotoapparat
            .with(applicationContext)
            .into(fotoapparatCameraView)
            .frameProcessor {
                initFaceDetector(it)
            }
//            .logger(
//                loggers(
//                    logcat(),
//                    fileLogger(applicationContext)
//                )
//            )
            .build()


        analyzeFloatActionBtn.setOnClickListener {


            val intentToAnalyzeActivity =
                Intent(applicationContext, AnalyzationActivity::class.java)



            fotoapparatInstance!!.takePicture().toBitmap().whenAvailable {
                val bitmap = rotateBitmap(it!!.bitmap)



                saveImg(ImageConverter.BitMapToString(bitmap))

                startActivity(intentToAnalyzeActivity)
            }


        }


        flipViewFloatActionBtn.setOnClickListener {

            fun cameraToFront() {
                isFront = true
                val cameraConfig = CameraConfiguration()
                fotoapparatInstance!!.switchTo(

                    lensPosition = front(),
                    cameraConfiguration = cameraConfig
                )
            }

            fun cameraToBack() {
                isFront = false
                val cameraConfig = CameraConfiguration()
                fotoapparatInstance!!.switchTo(

                    lensPosition = back(),
                    cameraConfiguration = cameraConfig
                )
            }

            if (isFront) cameraToBack() else cameraToFront()
        }

        fotoapparatCameraView.setOnClickListener {

            fotoapparatInstance!!.focus()
            //  Toast.makeText(applicationContext, "Clicked !", Toast.LENGTH_SHORT).show()

        }
    }

    private fun initFaceDetector(currentFrame: Frame) {

        val faceDetector = FaceDetector.create(this)
        // CODE FOR DETECTORS


        // fotoapparat face detector
        val detectedFaces =
            faceDetector.detectFaces(
                currentFrame.image,
                currentFrame.size.width,
                currentFrame.size.height,
                currentFrame.rotation
            )


        // set rectangle view to face
        runOnUiThread {
            camera_rectangle_view.setRectangles(detectedFaces)
        }

    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun saveImg(bitmapInStr: String) {
        val sp = getSharedPreferences(
            resources.getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE
        )

        val editor = sp.edit()

        editor.putString(resources.getString(R.string.image_bitmap_key), bitmapInStr)
        editor.apply()
    }

    fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()

        matrix.postRotate(90f)

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1080, true)

        val rotatedBitmap = Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
        return rotatedBitmap
    }
}

