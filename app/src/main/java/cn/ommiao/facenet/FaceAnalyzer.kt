package cn.ommiao.facenet

import android.graphics.*
import android.os.Environment
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.FaceFeature
import cn.ommiao.facenet.model.FaceRect
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FaceAnalyzer(
    private val onFaceDetected: (List<DetectedFace>) -> Unit,
    private val onAnalyseFinished: () -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {

        savePreviewImage(image)

        image.close()

        onFaceDetected(
            listOf(
                DetectedFace(
                    faceFeature = FaceFeature(label = "mockName"),
                    faceRect = FaceRect(
                        300, 500, 600, 900
                    )
                )
            )
        )

        onAnalyseFinished()
    }

    private fun savePreviewImage(image: ImageProxy) {
        image.convertImageProxyToBitmap()?.let { bitmap ->
            val file = File(
                Environment.getExternalStorageDirectory(),
                "/Download/FaceNet/preview-${image.imageInfo.timestamp}.jpg"
            )
            file.parentFile?.mkdirs()
            file.createNewFile()
            val fos = FileOutputStream(file)
            fos.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }
    }

    private fun ImageProxy.convertImageProxyToBitmap(): Bitmap? {
        val yBuffer = planes[0].buffer // Y
        val vuBuffer = planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val originBitmap = BitmapFactory.decodeByteArray(
            out.toByteArray(),
            0,
            out.toByteArray().size
        )
        val matrix = Matrix()
        matrix.postRotate(imageInfo.rotationDegrees.toFloat())
        return Bitmap.createBitmap(
            originBitmap,
            0,
            0,
            originBitmap.width,
            originBitmap.height,
            matrix,
            true
        )
    }

}