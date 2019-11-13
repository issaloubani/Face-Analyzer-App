package com.example.faceanalyserapp.Modules

import android.content.Context

class FaceDetector {

    companion object {
        fun with(context: Context): FaceDetectorBuilder = FaceDetectorBuilder(context)
    }
}