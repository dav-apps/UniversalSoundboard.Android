package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.adapters.PlayingSoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import java.io.File
import java.io.InputStream
import java.util.*

class MainViewModel : ViewModel(){
    var categoryListAdapter: CategoryListAdapter? = null
    var playingSoundListAdapter: PlayingSoundListAdapter? = null

    suspend fun copySoundFile(fileUri: Uri, contentResolver: ContentResolver, cacheDir: File){
        // Get the name
        val fileNameWithExt = fileUri.pathSegments.last().substringAfterLast("/")
        var fileName = fileNameWithExt

        val cursor = contentResolver.query(fileUri, null, null, null, null) ?: return
        cursor.use { cursor ->
            if(cursor.moveToFirst()){
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        cursor.close()
        fileName = fileName.replaceAfterLast(".", "").dropLast(1)

        val stream = contentResolver.openInputStream(fileUri) ?: return
        val file = File(cacheDir.path + "/" + fileNameWithExt)
        file.copyInputStreamToFile(stream)

        // Create the sound
        val currentCategory = FileManager.itemViewHolder.currentCategory
        val category: UUID? = if(currentCategory.uuid == Category.allSoundsCategory.uuid) null else currentCategory.uuid

        FileManager.addSound(null, fileName, category, file)
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}