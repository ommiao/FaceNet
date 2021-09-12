package cn.ommiao.facenet.extension

import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector

val CameraProvider.isSwitchCameraEnabled: Boolean
    get() = run {
        hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) && hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    }