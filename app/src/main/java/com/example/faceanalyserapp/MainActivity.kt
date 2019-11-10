package com.example.faceanalyserapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.faceanalyserapp.Modules.ImageConverter
import com.example.faceanalyserapp.Modules.Permissions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.facedetector.FaceDetector
import io.fotoapparat.log.fileLogger
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.preview.Frame
import io.fotoapparat.selector.*
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

//        fotoapparatInstance = Fotoapparat
//            .with(applicationContext)
//            .into(fotoapparatCameraView)
//            .frameProcessor {
//                initFaceDetector(it)
//            }
//            .logger(
//                loggers(
//                    logcat(),
//                    fileLogger(applicationContext)
//                )
//            )
//            .build()

        val cameraConfiguration = CameraConfiguration(
            pictureResolution = highestResolution(), // (optional) we want to have the highest possible photo resolution
            previewResolution = highestResolution(), // (optional) we want to have the highest possible preview resolution
            previewFpsRange = highestFps(),          // (optional) we want to have the best frame rate
            focusMode = firstAvailable(              // (optional) use the first focus mode which is supported by device
                continuousFocusPicture(),
                autoFocus(),                       // if continuous focus is not available on device, auto focus will be used
                fixed()                            // if even auto focus is not available - fixed focus mode will be used
            ),
            flashMode = firstAvailable(              // (optional) similar to how it is done for focus mode, this time for flash
                autoRedEye(),
                autoFlash(),
                torch(),
                off()
            ),
            antiBandingMode = firstAvailable(       // (optional) similar to how it is done for focus mode & flash, now for anti banding
                auto(),
                hz50(),
                hz60(),
                none()
            ),
            jpegQuality = manualJpegQuality(90),     // (optional) select a jpeg quality of 90 (out of 0-100) values
            sensorSensitivity = lowestSensorSensitivity(), // (optional) we want to have the lowest sensor sensitivity (ISO)
            frameProcessor = { frame -> initFaceDetector(frame) }            // (optional) receives each frame from preview stream
        )

        fotoapparatInstance = Fotoapparat(
            context = this,
            view = fotoapparatCameraView,                   // view which will draw the camera preview
            scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
            lensPosition = back(),               // (optional) we want back camera
            cameraConfiguration = cameraConfiguration, // (optional) define an advanced configuration
            logger = loggers(                    // (optional) we want to log camera events in 2 places at once
                logcat(),                   // ... in logcat
                fileLogger(this)            // ... and to file
            ),
            cameraErrorCallback = { error ->
                Log.println(
                    Log.ERROR,
                    "Camera Error :",
                    error.toString()
                )
            }   // (optional) log fatal errors
        )

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

    private fun saveImg(bitmapInStr: String) {
        val sp = getSharedPreferences(
            resources.getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE
        )

        val editor = sp.edit()

        editor.putString(resources.getString(R.string.image_bitmap_key), bitmapInStr)
        editor.apply()
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()

        matrix.postRotate(90f)

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1080, 1080, true)

        return Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
    }
}