package cn.ommiao.facenet

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class FaceAnalyzer(
    private val onFaceDetected: () -> Unit,
    private val onAnalyseFinished: () -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.close()
        onAnalyseFinished()
    }

}