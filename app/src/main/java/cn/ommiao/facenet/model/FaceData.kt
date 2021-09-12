package cn.ommiao.facenet.model

data class DetectedFace(
    val faceRect: FaceRect,
    val faceFeature: FaceFeature
)

data class FaceRect(
    val left:Int,
    val top: Int,
    val right: Int,
    val bottom: Int
)

data class FaceFeature(
    val label: String = "Unknown",
    val feature:FloatArray = FloatArray(512)
)