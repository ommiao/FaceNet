package cn.ommiao.facenet

import android.content.Context
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import cn.ommiao.facenet.model.AppDatabase
import cn.ommiao.facenet.model.DetectedFace
import cn.ommiao.facenet.model.SavedFace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    var isSwitchCameraEnabled by mutableStateOf(false)
        private set

    var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_BACK)
        private set

    var cameraRotation by mutableStateOf(Surface.ROTATION_0)
        private set

    var detectedFaces by mutableStateOf(listOf<DetectedFace>())

    val savedFaces by mutableStateOf(mutableStateListOf<SavedFace>())

    private lateinit var db: AppDatabase

    fun initDatabase(context: Context) {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "face_database"
        ).build()
    }

    fun switchCamera() {
        if (isSwitchCameraEnabled) {
            lensFacing =
                if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        }
    }

    fun initCameraLensFacing(isSwitchCameraEnabled: Boolean, lensFacing: Int) {
        this.isSwitchCameraEnabled = isSwitchCameraEnabled
        this.lensFacing = lensFacing
    }

    fun cameraOrientationChanged(orientation: Int) {
        cameraRotation = orientation
    }

    fun addSavedFaces(newSavedFaces: List<SavedFace>) {
        savedFaces.addAll(newSavedFaces)
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                newSavedFaces.forEach {
                    db.savedFaceDao().insertAll(it)
                }
            }
        }
    }

    fun initSavedFaces() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                savedFaces.addAll(db.savedFaceDao().getAll())
            }
        }
    }

}