package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound

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
    val closeDrawerData = MutableLiveData<Boolean>()
    val closeDrawer: LiveData<Boolean>
        get() = closeDrawerData

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }

    override fun onItemClicked(category: Category) {
        navigateToCategory(category)
    }

    fun navigateToCategory(category: Category){
        FileManager.itemViewHolder.currentCategory = category.uuid
        FileManager.itemViewHolder.setTitle(category.name ?: "")
        closeDrawerData.value = true
    }

    fun drawerClosed(){
        closeDrawerData.value = false
    }
}