package cn.ommiao.facenet

import android.content.res.AssetManager
import android.graphics.*
import android.os.Environment
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import cn.ommiao.facenet.face.Facenet
import cn.ommiao.facenet.face.MTCNN
import cn.ommiao.facenet.face.Utils
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.FaceFeature
import cn.ommiao.facenet.model.SavedFace
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class FaceAnalyzer(
    private val screenSize: Size,
    private val assetManager: AssetManager,
    private val onFaceDetected: (List<DetectedFace>) -> Unit,
    private val onFaceSaved: (List<SavedFace>) -> Unit
) : ImageAnalysis.Analyzer {

    var lensFacing = CameraSelector.LENS_FACING_BACK

    var captureNextFaces = false

    private var captureTryTimes = 0

    private lateinit var mtcnn: MTCNN

    private lateinit var facenet: Facenet

    override fun analyze(image: ImageProxy) {

        if (this::mtcnn.isInitialized.not() || this::facenet.isInitialized.not()) {
            mtcnn = MTCNN(assetManager)
            facenet = Facenet(assetManager)
            image.close()
            return
        }

        val liveFacesList = mutableListOf<DetectedFace>()

        val savedFacesList = mutableListOf<SavedFace>()

        if (captureNextFaces) {
            captureTryTimes += 1
        }

        image.convertImageProxyToBitmap()?.let { bitmap ->
            val faceBoxes = mtcnn.detectFaces(bitmap, 40)
            faceBoxes.forEach { box ->
                val rect: Rect = box.transform2Rect()
                Utils.rectExtend(bitmap, rect, 5)

                if (captureNextFaces) {
                    val bitmapFaceCropped: Bitmap = Utils.crop(bitmap, rect)
                    val name = System.currentTimeMillis().toString()
                    val path =
                        "${Environment.getExternalStorageDirectory()}/Download/FaceNet/${name}.jpg"
                    saveBitmap(bitmapFaceCropped, path)
                    savedFacesList.add(
                        SavedFace(
                            label = "None",
                            filePath = path
                        )
                    )
                }

                convertToScreenSize(
                    rect = rect,
                    imageWidth = bitmap.width,
                    imageHeight = bitmap.height
                )

                liveFacesList.add(
                    DetectedFace(
                        faceRect = rect,
                        faceFeature = FaceFeature()
                    )
                )

            }
        }

        onFaceDetected(liveFacesList)

        image.close()

        if (captureNextFaces) {
            onFaceSaved(savedFacesList)
        }

        if (savedFacesList.isNotEmpty() || captureTryTimes >= 5){
            captureTryTimes = 0
            captureNextFaces = false
        }
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

    private fun saveBitmap(bitmap: Bitmap, path: String) {
        val file = File(path)
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