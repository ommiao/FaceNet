package cn.ommiao.facenet

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    var isSwitchCameraEnabled by mutableStateOf(false)

    var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)

    fun switchCamera() {
        if (isSwitchCameraEnabled){
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        }
    }

}