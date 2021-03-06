package cn.ommiao.facenet.ui.composable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.ommiao.facenet.MainViewModel
import cn.ommiao.facenet.R
import cn.ommiao.facenet.extension.clickableWithoutRipple
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.imePadding
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SheetContent(actionRowHeight: Dp, expandFraction: Float, onCaptureFaceClick: () -> Unit) {
    ProvideWindowInsets {
        val sheetContentHeight = 350.dp
        val offsetY = actionRowHeight * (1 - expandFraction)
        val actionRowAlpha = 1.0f * (1 - expandFraction * 3f)
        Box(modifier = Modifier
            .imePadding()
            .height(sheetContentHeight)) {
            ActionRow(actionRowHeight, actionRowAlpha, onCaptureFaceClick)
            FacesSurface(offsetY, sheetContentHeight, expandFraction)
        }
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
        FacesSurfaceTitle(offsetY * 1.25f)
        FacesLazyRow(offsetY * 1.5f)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FacesLazyRow(offsetY: Dp) {
    val viewModel: MainViewModel = viewModel()
    val savedFaces = viewModel.allSavedFaces
    if (savedFaces.isEmpty()) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Text(
                text = "Empty",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        LazyRow(
            modifier = Modifier
                .offset(y = offsetY)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            items(savedFaces) { savedFace ->
                val focusManager = LocalFocusManager.current
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .combinedClickable(onLongClick = {
                            viewModel.deleteSavedFace(savedFace)
                        }) {
                            focusManager.clearFocus()
                        },
                    shape = RoundedCornerShape(25.dp),
                    elevation = 2.dp
                ) {
                    GlideImage(
                        modifier = Modifier.fillMaxSize(),
                        imageModel = savedFace.filePath,
                        contentScale = ContentScale.Crop
                    )
                    Box {
                        var label by remember { mutableStateOf(savedFace.label) }
                        BasicTextField(
                            value = label,
                            onValueChange = { label = it },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp)
                                .onFocusChanged { focusState ->
                                    if (focusState.hasFocus.not() && label != savedFace.label) {
                                        viewModel.updateSavedFaceLabel(savedFace = savedFace, label)
                                    }
                                },
                            textStyle = TextStyle.Default.copy(textAlign = TextAlign.Center),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                focusManager.clearFocus()
                            })
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FacesSurfaceTitle(offsetY: Dp) {
    val viewModel: MainViewModel = viewModel()
    val facesSize = viewModel.allSavedFaces.size
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
private fun ActionRow(height: Dp, alpha: Float, onCaptureFaceClick: () -> Unit) {
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
        CaptureFaceButton(onCaptureFaceClick)
        SwitchCameraButton()
    }
}

@Composable
private fun CaptureFaceButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_shutter_normal),
        contentDescription = "shutter",
        modifier = Modifier
            .size(92.dp)
            .padding(4.dp)
            .clickableWithoutRipple(onClick = onClick),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun SwitchCameraButton() {
    val viewModel: MainViewModel = viewModel()
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