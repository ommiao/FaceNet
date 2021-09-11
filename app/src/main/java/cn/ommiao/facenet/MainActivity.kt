package cn.ommiao.facenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import cn.ommiao.facenet.extension.currentFraction
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
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetBackgroundColor = Color.Transparent,
                    sheetElevation = 0.dp,
                    sheetContent = {
                        SheetContent(sheetPeekHeight, scaffoldState.currentFraction)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Gray)
                    ) {
                        Text(text = "Content of Sheet", modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }

    @Composable
    private fun SheetContent(actionRowHeight: Dp, expandFraction: Float) {
        val sheetContentHeight = 350.dp
        val offsetY = actionRowHeight * (1 - expandFraction)
        val actionRowAlpha = 1.0f * (1 - expandFraction)
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
        Surface(
            modifier = Modifier
                .offset(y = offsetY)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .fillMaxWidth()
                .height(sheetContentHeight)
                .alpha(expandFraction), elevation = 8.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FacesSurfaceSpinner(expandFraction)
                val facesSize = 10
                FacesSurfaceTitle(facesSize)
                FacesLazyRow(facesSize)
            }
        }
    }

    @Composable
    private fun FacesSurfaceSpinner(expandFraction: Float) {
        Spacer(
            modifier = Modifier
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color = Color.Black)
                .height(4.dp)
                .width((expandFraction * 40).dp)
        )
    }

    @Composable
    private fun FacesLazyRow(facesSize: Int) {
        LazyRow(
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
    private fun FacesSurfaceTitle(facesSize: Int) {
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
}