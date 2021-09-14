package cn.ommiao.facenet

import android.graphics.*
import android.os.Environment
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cn.ommiao.facenet.face.MTCNN
import cn.ommiao.facenet.face.Utils
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.FaceFeature
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FaceAnalyzer(
    private val screenSize: Size,
    private val mtcnn: MTCNN,
    private val onFaceDetected: (List<DetectedFace>) -> Unit,
    private val onAnalyseFinished: () -> Unit
) : ImageAnalysis.Analyzer {

    var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun analyze(image: ImageProxy) {

        val faceList = mutableListOf<DetectedFace>()

        image.convertImageProxyToBitmap()?.let { bitmap ->
            val faceBoxes = mtcnn.detectFaces(bitmap, 40)
            faceBoxes.forEach { box ->
                val rect: Rect = box.transform2Rect()
                Utils.rectExtend(bitmap, rect, 5)

                Utils.drawRect(bitmap, rect, 3)

//                val bitmapFaceCropped: Bitmap = Utils.crop(bitmap, rect)
//                saveBitmap(bitmapFaceCropped, System.currentTimeMillis().toString())

                convertToScreenSize(
                    rect = rect,
                    imageWidth = bitmap.width,
                    imageHeight = bitmap.height
                )

                faceList.add(
                    DetectedFace(
                        faceRect = rect,
                        faceFeature = FaceFeature()
                    )
                )

            }
        }

        onFaceDetected(faceList)

        image.close()

        onAnalyseFinished()
    }

    private fun convertToScreenSize(rect: Rect, imageWidth: Int, imageHeight: Int) {
        val screenRatio = screenSize.width.toFloat() / screenSize.height.toFloat()
        val imageRatio = imageWidth.toFloat() / imageHeight.toFloat()
        if (imageRatio > screenRatio) {
            val scaleRatio = screenSize.height.toFloat() / imageHeight.toFloat()
            val targetImageWidth = (scaleRatio * imageWidth).toInt()
            with(rect) {
                left = (left * scaleRatio - (targetImageWidth - screenSize.width) / 2).toInt()
                top = (top * scaleRatio).toInt()
                right = (right * scaleRatio - (targetImageWidth - screenSize.width) / 2).toInt()
                bottom = (bottom * scaleRatio).toInt()
                val left0 = left
                val right0 = right
                if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                    left = screenSize.width - right0
                    right = screenSize.width - left0
                }
            }
        }
    }

    private fun saveBitmap(bitmap: Bitmap, name: String) {
        val file = File(
            Environment.getExternalStorageDirectory(),
            "/Download/FaceNet/${name}.jpg"
        )
        file.parentFile?.mkdirs()
        file.createNewFile()
        val fos = FileOutputStream(file)
        fos.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
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