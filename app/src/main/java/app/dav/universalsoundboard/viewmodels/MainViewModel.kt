package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.io.InputStream
import java.util.*

class MainViewModel : ViewModel(){
    var categoryListAdapter: CategoryListAdapter? = null

    fun copySoundFile(fileUri: Uri, contentResolver: ContentResolver, cacheDir: File){
        // Get the name
        val fileNameWithExt = fileUri.pathSegments.last().substringAfterLast("/")
        var fileName = fileNameWithExt

        val cursor = contentResolver.query(fileUri, null, null, null, null)
        try{
            if(cursor.moveToFirst()){
                fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }finally {
            cursor.close()
        }
        fileName = fileName.replaceAfterLast(".", "").dropLast(1)

        val stream = contentResolver.openInputStream(fileUri)
        val file = File(cacheDir.path + "/" + fileNameWithExt)
        file.copyInputStreamToFile(stream)

        // Create the sound
        val currentCategory = FileManager.itemViewHolder.currentCategory
        val category: UUID? = if(currentCategory == Category.allSoundsCategory.uuid) null else currentCategory

        GlobalScope.launch(Dispatchers.Main) { FileManager.addSound(null, fileName, category, file) }
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}