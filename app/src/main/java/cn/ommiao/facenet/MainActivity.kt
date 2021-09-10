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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
                val radius = (40 * scaffoldState.currentFraction).dp
                val sheetBackgroundColor =
                    Color.White.copy(alpha = scaffoldState.currentFraction)
                val sheetElevation =
                    if (scaffoldState.bottomSheetState.currentValue == BottomSheetValue.Expanded &&
                        scaffoldState.bottomSheetState.targetValue == BottomSheetValue.Expanded
                    ) BottomSheetScaffoldDefaults.SheetElevation else 0.dp
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = sheetPeekHeight,
                    sheetShape = RoundedCornerShape(topStart = radius, topEnd = radius),
                    sheetBackgroundColor = sheetBackgroundColor,
                    sheetElevation = sheetElevation,
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
    private fun SheetContent(sheetPeekHeight: Dp, currentFraction: Float) {
        val contentHeight = 350.dp
        val offsetY = sheetPeekHeight * (1 - currentFraction)
        Box(modifier = Modifier.height(contentHeight)) {
            val actionRowAlpha = 1.0f * (1 - currentFraction)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetPeekHeight)
                    .padding(vertical = 25.dp)
                    .alpha(actionRowAlpha),
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
            Column(
                modifier = Modifier
                    .offset(y = offsetY)
                    .fillMaxWidth()
                    .height(contentHeight)
            ) {
                Text(
                    text = "FACES",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 20.dp, start = 20.dp, end = 20.dp)
                ) {
                    repeat(10) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(200.dp),
                                shape = RoundedCornerShape(25.dp),
                                elevation = 2.dp
                            ) {
                                Box {
                                    Text(text = "Face", modifier = Modifier.align(Alignment.Center))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}