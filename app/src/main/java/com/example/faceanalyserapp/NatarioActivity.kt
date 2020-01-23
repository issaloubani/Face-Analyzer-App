package com.example.faceanalyserapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.example.faceanalyserapp.Modules.AwesomeFrameProcessor
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Grid
import kotlinx.android.synthetic.main.activity_natario.*
import kotlinx.android.synthetic.main.buttons_layout.*
import kotlinx.android.synthetic.main.frame_processors_buttons_layout.*


class NatarioActivity : AppCompatActivity() {
    private val awesomeFrameProcessor: AwesomeFrameProcessor? by lazy {
        AwesomeFrameProcessor(
            applicationContext
            , natarioCameraView,
            natarioCameraImageView
        )
    }
    override fun onResume() {
        super.onResume()

        natarioCameraView.setLifecycleOwner(this)
        natarioCameraView.addFrameProcessor(
            awesomeFrameProcessor
        )

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_natario)

        // buttons events handler
        rotateBtn.setOnClickListener {
            natarioCameraView.toggleFacing()
        }

        gridBtn.setOnClickListener {
            natarioCameraView.grid =
                if (natarioCameraView.grid == Grid.OFF) Grid.DRAW_3X3 else Grid.OFF
        }

        flashBtn.setOnClickListener {

            natarioCameraView.flash =
                if (natarioCameraView.flash == Flash.ON) Flash.OFF else Flash.ON
        }

        toggleFacialTrackingBtn.setOnClickListener {

            // init animation
            val avd = AnimatedVectorDrawableCompat.create(
                this, if (!awesomeFrameProcessor!!.facialTracking)
                    R.drawable.enable_facial else R.drawable.disable_facial
            )


            // set img drawable
            toggleFacialTrackingBtn.setImageDrawable(avd)

            // start animation
            avd!!.start()

            // toggle flag
            awesomeFrameProcessor!!.facialTracking = !awesomeFrameProcessor!!.facialTracking
        }
    }
}
