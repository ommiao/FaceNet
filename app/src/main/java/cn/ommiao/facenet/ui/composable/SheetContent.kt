package cn.ommiao.facenet.ui.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ommiao.facenet.MainViewModel
import cn.ommiao.facenet.extension.clickableWithoutRipple
import cn.ommiao.facenet.R

@Composable
fun SheetContent(actionRowHeight: Dp, expandFraction: Float) {
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
        SwitchCameraButton()
        CaptureFaceButton()
        SwitchCameraButton()
    }
}

@Composable
private fun CaptureFaceButton() {
    val viewModel:MainViewModel = viewModel()
    Image(
        painter = painterResource(id = R.drawable.ic_shutter_normal),
        contentDescription = "shutter",
        modifier = Modifier
            .size(92.dp)
            .padding(4.dp)
            .clickableWithoutRipple {
                viewModel.startCollectFaces()
            },
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun SwitchCameraButton() {
    val viewModel:MainViewModel = viewModel()
    Image(
        painter = painterResource(id = R.drawable.ic_switch),
        contentDescription = "switch",
        modifier = Modifier
            .size(64.dp)
            .padding(4.dp)
            .alpha(if (viewModel.isSwitchCameraEnabled) 1f else 0f)
            .clickableWithoutRipple {
                viewModel.switchCamera()
            },
        contentScale = ContentScale.Crop
    )
}