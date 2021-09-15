package cn.ommiao.facenet

import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.SavedFace

class MainViewModel : ViewModel() {

    var isSwitchCameraEnabled by mutableStateOf(false)
    private set

    var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)
    private set

    var cameraRotation by mutableStateOf(Surface.ROTATION_0)
    private set

    var detectedFaces by mutableStateOf(listOf<DetectedFace>())

    val savedFaces by mutableStateOf(mutableStateListOf<SavedFace>())

    fun switchCamera() {
        if (isSwitchCameraEnabled) {
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        }
    }

    fun initCameraLensFacing(isSwitchCameraEnabled: Boolean, lensFacing: Int){
        this.isSwitchCameraEnabled = isSwitchCameraEnabled
        this.lensFacing = lensFacing
    }

    fun cameraOrientationChanged(orientation: Int){
        cameraRotation = orientation
    }

    fun addSavedFaces(savedFaces: List<SavedFace>){
        this.savedFaces.addAll(savedFaces)
    }

}