package cn.ommiao.facenet.model

import android.graphics.Rect
import kotlin.math.sqrt

data class DetectedFace(
    val faceRect: Rect,
    val faceFeature: FaceFeature
)

const val DIMS = 512
data class FaceFeature(
    val label: String = "Unknown",
    val feature: FloatArray = FloatArray(DIMS)
) {

    fun similarWith(other: FaceFeature): Double {
        val dist = feature.indices.sumOf { i ->
            ((feature[i] - other.feature[i]) * (feature[i] - other.feature[i])).toDouble()
        }
        return sqrt(dist)
    }

}

data class SavedFace(
    val label: String,
    val filePath: String
)