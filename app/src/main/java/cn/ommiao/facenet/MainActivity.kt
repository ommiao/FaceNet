package cn.ommiao.facenet

import android.os.Bundle
import android.util.Size
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.window.WindowManager
import cn.ommiao.facenet.extension.expandFraction
import cn.ommiao.facenet.extension.isSwitchCameraEnabled
import cn.ommiao.facenet.ui.composable.CameraPreview
import cn.ommiao.facenet.ui.composable.DetectedFacesView
import cn.ommiao.facenet.ui.composable.SheetContent
import cn.ommiao.facenet.ui.theme.FaceNetTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var faceAnalyzer: FaceAnalyzer? = null

    private fun getFaceAnalyzer(): FaceAnalyzer {
        val screenRect = WindowManager(this).getCurrentWindowMetrics().bounds
        return faceAnalyzer ?: FaceAnalyzer(
            screenSize = Size(screenRect.width(), screenRect.height()),
            assetManager = assets,
            allSavedFaces = viewModel.allSavedFaces,
            onFaceDetected = {
                viewModel.detectedFaces = it
            }) {
            viewModel.addSavedFaces(it)
            showSavedToast(it.size)
        }.apply {
            faceAnalyzer = this
        }
    }

    private fun showSavedToast(size: Int) {
        runOnUiThread {
            Toast.makeText(this, "$size face(s) saved.", LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.initDatabase(this)
        viewModel.initSavedFaces()
        setContent {
            SystemUiController()
            FaceNetTheme {
                val cameraProviderFuture =
                    remember { ProcessCameraProvider.getInstance(this@MainActivity) }
                initCameraLensFacing(cameraProviderFuture)
                val scaffoldState = rememberBottomScaffoldState()
                AutoClearFocus(scaffoldState)
                val sheetPeekHeight = 150.dp
                val sheetBackgroundColor =
                    Color.White.copy(alpha = scaffoldState.expandFraction)
                val sheetElevation = if (scaffoldState.expandFraction == 1f) 8.dp else 0.dp
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetBackgroundColor = sheetBackgroundColor,
                    sheetElevation = sheetElevation,
                    sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    sheetContent = {
                        SheetContent(sheetPeekHeight, scaffoldState.expandFraction) {
                            faceAnalyzer?.captureNextFaces = true
                        }
                    }
                ) {
                    Box {
                        CameraPreview(getFaceAnalyzer())
                        DetectedFacesView(viewModel.detectedFaces)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun rememberBottomScaffoldState() = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Collapsed,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    )

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun AutoClearFocus(scaffoldState: BottomSheetScaffoldState) {
        val focusManager = LocalFocusManager.current
        if (scaffoldState.bottomSheetState.targetValue == BottomSheetValue.Collapsed) {
            focusManager.clearFocus()
        }
    }

    private fun initCameraLensFacing(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        val cameraProvider = cameraProviderFuture.get()
        val isSwitchCameraEnabled = cameraProvider.isSwitchCameraEnabled
        var lensFacing = CameraSelector.LENS_FACING_BACK
        if (!cameraProvider.isSwitchCameraEnabled) {
            if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                lensFacing = CameraSelector.LENS_FACING_FRONT
            } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                lensFacing = CameraSelector.LENS_FACING_BACK
            }
        }
        viewModel.initCameraLensFacing(isSwitchCameraEnabled, lensFacing)
    }

    @Composable
    private fun SystemUiController() {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.isSystemBarsVisible = false
        }
    }
}

