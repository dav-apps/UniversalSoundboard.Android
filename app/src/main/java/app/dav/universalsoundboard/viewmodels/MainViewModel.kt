package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentResolver
import android.net.Uri
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import java.io.File
import java.io.InputStream
import java.util.*

class MainViewModel :
        ViewModel(),
        CategoryListAdapter.OnItemClickListener{

    private var onCategoryClickListener: CategoryListAdapter.OnItemClickListener = this
    val categoryListAdapter = CategoryListAdapter(onCategoryClickListener)
    val closeDrawerData = MutableLiveData<Boolean>()
    val closeDrawer: LiveData<Boolean>
        get() = closeDrawerData

    override fun onItemClicked(category: Category) {
        FileManager.showCategory(category)
        closeDrawerData.value = true
    }

    fun drawerClosed(){
        closeDrawerData.value = false
    }

    fun copySoundFile(fileUri: Uri, contentResolver: ContentResolver, cacheDir: File){
        val fileNameWithExt = fileUri.pathSegments.last().substringAfterLast("/")
        val fileName = fileNameWithExt.replaceAfterLast(".", "").dropLast(1)

        val stream = contentResolver.openInputStream(fileUri)
        val file = File(cacheDir.path + "/" + fileNameWithExt)
        file.copyInputStreamToFile(stream)

        // Create the sound
        val currentCategory = FileManager.itemViewHolder.currentCategory
        val category: UUID? = if(currentCategory == Category.allSoundsCategory.uuid) null else currentCategory

        FileManager.addSound(null, fileName, category, file)
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }
}