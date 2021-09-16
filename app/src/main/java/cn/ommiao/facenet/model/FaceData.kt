package cn.ommiao.facenet.model

import android.graphics.Point
import android.graphics.Rect
import androidx.room.*
import java.util.*

data class DetectedFace(
    val faceRect: Rect,
    val facePoints: Array<Point>,
    val faceFeature: FaceFeature
)

const val DIMS = 512

data class FaceFeature(
    val label: String = "Unknown",
    val feature: FloatArray = FloatArray(DIMS)
)

@Entity
data class SavedFace(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "feature") val feature: FloatArray = FloatArray(DIMS)
)

@Dao
interface SavedFaceDao {
    @Query("SELECT * FROM savedface")
    fun getAll(): List<SavedFace>

    @Insert
    fun insertAll(vararg faces: SavedFace)

    @Delete
    fun delete(face: SavedFace)
}

@Database(entities = [SavedFace::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedFaceDao(): SavedFaceDao
}

object Converters {
    @TypeConverter
    fun fromFloatArray(array: FloatArray): String {
        return array.joinToString(separator = ",") { it.toString() }
    }

    @TypeConverter
    fun fromString(value: String): FloatArray {
        val array = FloatArray(DIMS)
        value.split(",").map { it.toFloat() }.forEachIndexed { i, f -> array[i] = f }
        return array
    }
}