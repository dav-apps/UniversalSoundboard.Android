package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category

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
}