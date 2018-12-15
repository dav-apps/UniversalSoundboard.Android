package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class SoundViewModel : ViewModel(){
    var soundListAdapter: SoundListAdapter? = null

    fun changeSoundImage(uri: Uri, contentResolver: ContentResolver, sound: Sound, cacheDir: File){
        // Get the name of the file
        val fileNameWithExt = uri.pathSegments.last().substringAfterLast("/")
        var fileName = fileNameWithExt

        val cursor = contentResolver.query(uri, null, null, null, null)
        try{
            if(cursor?.moveToFirst() == true){
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }finally {
            cursor?.close()
        }
        fileName = fileName.replaceAfterLast(".", "").dropLast(1)

        val stream = contentResolver.openInputStream(uri) ?: return
        val file = File(cacheDir.path + "/" + fileNameWithExt)
        file.copyInputStreamToFile(stream)

        // Create the imageFile table object and update the sound
        GlobalScope.launch(Dispatchers.Main) { FileManager.updateImageOfSound(sound.uuid, file) }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}