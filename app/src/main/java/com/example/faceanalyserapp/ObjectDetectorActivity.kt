package com.example.faceanalyserapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.faceanalyserapp.Modules.TensorflowProcessor
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import kotlinx.android.synthetic.main.activity_object_detector.*

class ObjectDetectorActivity : AppCompatActivity(), FrameProcessor {
    override fun process(frame: Frame) {


    }


    override fun onResume() {
        super.onResume()
        objectDetectorCameraView.setLifecycleOwner(this)
        objectDetectorCameraView.addFrameProcessor(
            TensorflowProcessor(
                applicationContext,
                objectDetectorCameraView,
                objectDetectorOverlay
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_object_detector)
    }
}
