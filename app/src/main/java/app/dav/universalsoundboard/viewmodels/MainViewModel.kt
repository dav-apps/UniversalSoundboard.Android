package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import java.util.*

class MainViewModel :
        ViewModel(),
        SoundListAdapter.OnItemClickListener,
        SoundListAdapter.OnItemLongClickListener,
        CategoryListAdapter.OnItemClickListener{

    private var onSoundClickListener: SoundListAdapter.OnItemClickListener = this
    private var onSoundLongClickListener: SoundListAdapter.OnItemLongClickListener = this
    private var onCategoryClickListener: CategoryListAdapter.OnItemClickListener = this
    val soundListAdapter = SoundListAdapter(onSoundClickListener, onSoundLongClickListener)
    val categoryListAdapter = CategoryListAdapter(onCategoryClickListener)
    var currentCategory: UUID? = null

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }

    override fun onItemClicked(category: Category) {

    }

    fun getSounds() : LiveData<ArrayList<Sound>> {
        return FileManager.getAllSounds()
    }

    fun getCategories() : LiveData<ArrayList<Category>>{
        return Transformations.map(FileManager.getAllCategories()) {
            it.add(0, Category.allSoundsCategory)
            it
        }
    }
}