package cn.ommiao.facenet.ui.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import cn.ommiao.facenet.model.DetectedFace

private val COLOR = Color.Red

private const val TEXT_SIZE = 44f
private const val BOX_STROKE_WIDTH = 4f
private const val POINT_STROKE_WIDTH = 6f

private val nativeTextPaint = Paint().apply {
    color = COLOR
}.asFrameworkPaint().apply {
    style = android.graphics.Paint.Style.FILL
    textSize = TEXT_SIZE
}

@Composable
fun DetectedFacesView(detectedFaces: List<DetectedFace>) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
    ) {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            detectedFaces.forEach { detectedFace ->
                nativeCanvas.drawText(
                    detectedFace.faceFeature.label,
                    detectedFace.faceRect.left.toFloat(),
                    detectedFace.faceRect.top.toFloat() - 10,
                    nativeTextPaint
                )
                with(detectedFace.faceRect) {
                    drawRect(
                        color = COLOR,
                        topLeft = Offset(x = left.toFloat(), y = top.toFloat()),
                        size = Size(
                            width = right.toFloat() - left.toFloat(),
                            height = bottom.toFloat() - top.toFloat()
                        ),
                        style = Stroke(width = BOX_STROKE_WIDTH)
                    )
                }
                with(detectedFace.facePoints) {
                    drawPoints(
                        points = this.map { point ->
                            Offset(
                                x = point.x.toFloat(),
                                y = point.y.toFloat()
                            )
                        },
                        pointMode = PointMode.Points,
                        color = COLOR,
                        strokeWidth = POINT_STROKE_WIDTH
                    )
                }
            }
        }
    }
}