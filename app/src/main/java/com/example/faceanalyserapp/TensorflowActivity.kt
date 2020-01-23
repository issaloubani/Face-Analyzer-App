package com.example.faceanalyserapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.faceanalyserapp.Modules.Label
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import kotlinx.android.synthetic.main.activity_tensorflow.*
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class TensorflowActivity : AppCompatActivity(), FrameProcessor {

    // vars

    private val PATH_TO_MODULE = "detect.tflite"
    private val PATH_TO_LABELS = "labelmap.txt"

    var objects = textToArrayList()
    val tflite: Interpreter by lazy {
        Interpreter(loadModuleFile(this))
    }

    override fun process(frame: Frame) {

        val arrayOfDoubles = ArrayList<Double>()

        tflite.run(frame.data, arrayOfDoubles)
        val labels = textToArrayList()
        var label = getHighestConfidence(arrayOfDoubles)
        label.apply {
            name = labels[index]
        }

        Log.println(Log.ASSERT, "Label Info", "Recognised as ${label.name}")


    }

    private fun getHighestConfidence(arrayList: ArrayList<Double>): Label {

        val max = arrayList.maxBy {
            it < 1 && it < arrayList.max()!!
        }

        return Label("", max!!, arrayList.indexOf(max))

    }

    private fun textToArrayList(): ArrayList<String> {

        val values = ArrayList<String>()
        try {
            val bReader = BufferedReader(InputStreamReader(assets.open(PATH_TO_LABELS)))

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

    private fun loadModuleFile(activity: Activity): MappedByteBuffer {

        val assetFileDescriptor = activity.assets.openFd(PATH_TO_MODULE)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensorflow)

        tensorflowCameraView.setLifecycleOwner(this)
        tensorflowCameraView.addFrameProcessor(this)
    }
}
