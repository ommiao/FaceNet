package cn.ommiao.facenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import cn.ommiao.facenet.extension.expandFraction
import cn.ommiao.facenet.ui.theme.FaceNetTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.isSystemBarsVisible = false
            }
            FaceNetTheme {
                val sheetPeekHeight = 150.dp
                val scaffoldState = rememberBottomSheetScaffoldState()
                val sheetBackgroundColor = Color.White.copy(alpha = scaffoldState.expandFraction)
                val sheetElevation = if (scaffoldState.expandFraction == 1f) 8.dp else 0.dp
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetBackgroundColor = sheetBackgroundColor,
                    sheetElevation = sheetElevation,
                    sheetShape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    sheetContent = {
                        SheetContent(sheetPeekHeight, scaffoldState.expandFraction)
                    }
                ) {
                    CameraPreview()
                }
            }
        }
    }

    @Composable
    private fun SheetContent(actionRowHeight: Dp, expandFraction: Float) {
        val sheetContentHeight = 350.dp
        val offsetY = actionRowHeight * (1 - expandFraction)
        val actionRowAlpha = 1.0f * (1 - expandFraction * 3f)
        Box(modifier = Modifier.height(sheetContentHeight)) {
            ActionRow(actionRowHeight, actionRowAlpha)
            FacesSurface(offsetY, sheetContentHeight, expandFraction)
        }
    }

    @Composable
    private fun FacesSurface(
        offsetY: Dp,
        sheetContentHeight: Dp,
        expandFraction: Float
    ) {
        Column(
            modifier = Modifier
                .alpha(expandFraction)
                .fillMaxWidth()
                .height(sheetContentHeight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FacesSurfaceSpinner(expandFraction, offsetY)
            val facesSize = 10
            FacesSurfaceTitle(facesSize, offsetY * 1.25f)
            FacesLazyRow(facesSize, offsetY * 1.5f)
        }
    }

    @Composable
    private fun FacesSurfaceSpinner(expandFraction: Float, offsetY: Dp) {
        Spacer(
            modifier = Modifier
                .offset(y = offsetY)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = Color.Black)
                .height(4.dp)
                .width((expandFraction * 40).dp)
        )
    }

    @Composable
    private fun FacesLazyRow(facesSize: Int, offsetY: Dp) {
        LazyRow(
            modifier = Modifier.offset(y = offsetY),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            repeat(facesSize) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(200.dp),
                        shape = RoundedCornerShape(25.dp),
                        elevation = 2.dp
                    ) {
                        Box {
                            Text(
                                text = "Face",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun FacesSurfaceTitle(facesSize: Int, offsetY: Dp) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 20.sp)) {
                    append("FACES / ")
                }
                withStyle(SpanStyle(fontSize = 16.sp)) {
                    append(facesSize.toString())
                }
            },
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(y = offsetY)
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
        )
    }

    @Composable
    private fun ActionRow(height: Dp, alpha: Float) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(vertical = 25.dp)
                .alpha(alpha),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_switch),
                contentDescription = "switch",
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(id = R.drawable.ic_shutter_normal),
                contentDescription = "shutter",
                modifier = Modifier
                    .size(92.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Image(
                painter = painterResource(id = R.drawable.ic_switch),
                contentDescription = "faces",
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
        }
    }


    @Composable
    fun CameraPreview() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}