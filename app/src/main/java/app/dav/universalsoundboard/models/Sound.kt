package app.dav.universalsoundboard.models

import android.graphics.Bitmap
import app.dav.universalsoundboard.data.FileManager
import java.io.File
import java.util.*

class Sound(val uuid: UUID, var name: String, var category: UUID?, var favourite: Boolean, var image: Bitmap?){
    suspend fun getAudioFile() : File? {
        return FileManager.getAudioFileOfSound(uuid)
    }
}