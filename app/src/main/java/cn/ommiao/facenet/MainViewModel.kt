package cn.ommiao.facenet

import android.content.Context
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

    var detectedFaces by mutableStateOf(listOf<DetectedFace>())

    val allSavedFaces by mutableStateOf(mutableStateListOf<SavedFace>())

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

    fun addSavedFaces(newSavedFaces: List<SavedFace>) {
        allSavedFaces.addAll(newSavedFaces)
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
                allSavedFaces.addAll(db.savedFaceDao().getAll())
            }
        }
    }

    fun deleteSavedFace(savedFace: SavedFace){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                allSavedFaces.remove(savedFace)
                db.savedFaceDao().delete(savedFace)
            }
        }
    }

    fun updateSavedFaceLabel(savedFace: SavedFace, newLabel: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val index = allSavedFaces.indexOf(savedFace)
                val copy = savedFace.copy(
                    label = newLabel
                )
                allSavedFaces[index] = copy
                db.savedFaceDao().update(copy)
            }
        }
    }

}