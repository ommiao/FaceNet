package cn.ommiao.facenet

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.FaceFeature
import cn.ommiao.facenet.model.FaceRect

class FaceAnalyzer(
    private val onFaceDetected: (List<DetectedFace>) -> Unit,
    private val onAnalyseFinished: () -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        image.close()

        onFaceDetected(listOf(
            DetectedFace(
                faceFeature = FaceFeature(label = "mockName"),
                faceRect = FaceRect(
                    300, 500, 600, 900
                )
            )
        ))

        onAnalyseFinished()
    }

}