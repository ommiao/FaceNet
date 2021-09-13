package cn.ommiao.facenet.ui.composable

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ommiao.facenet.MainViewModel
import com.google.common.util.concurrent.ListenableFuture

@Composable
fun CameraPreview(
    faceAnalyzer: ImageAnalysis.Analyzer
) {
    val context = LocalContext.current
    val cameraProviderFuture =
        remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: MainViewModel = viewModel()
    val metrics = LocalConfiguration.current.screenHeightDp
    key(viewModel.lensFacing) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                bindCameraUseCases(
                    ctx,
                    cameraProviderFuture,
                    previewView,
                    lifecycleOwner,
                    viewModel.lensFacing,
                    viewModel.cameraRotation,
                    faceAnalyzer
                )
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun bindCameraUseCases(
    ctx: Context,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    lensFacing: Int,
    cameraRotation: Int,
    faceAnalyzer: ImageAnalysis.Analyzer
) {
    val executor = ContextCompat.getMainExecutor(ctx)

    cameraProviderFuture.addListener(
        {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetRotation(cameraRotation)
                .build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(cameraRotation)
                .build().apply {
                    setAnalyzer(executor, faceAnalyzer)
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                analyzer,
                preview
            )
        },
        executor
    )
}