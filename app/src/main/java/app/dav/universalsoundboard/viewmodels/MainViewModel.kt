package app.dav.universalsoundboard.viewmodels

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.provider.Contacts
import android.util.Log
import app.dav.davandroidlibrary.Dav
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.launch
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
    var currentCategory: UUID = Category.allSoundsCategory.uuid
    val closeDrawerData = MutableLiveData<Boolean>()
    val closeDrawer: LiveData<Boolean>
        get() = closeDrawerData
    /*
    var soundsData = MutableLiveData<ArrayList<Sound>>()
    val soundsLiveData: LiveData<ArrayList<Sound>>
        get() = soundsData
    */
    //var liveData: LiveData<ArrayList<Sound>> = FileManager.getAllSounds()
    //val mutableLiveData = MutableLiveData<ArrayList<Sound>>()
    val mediatorLiveData = MediatorLiveData<ArrayList<Sound>>()

    override fun onItemClicked(sound: Sound) {

    }

    override fun onItemLongClicked(sound: Sound) {

    }

    override fun onItemClicked(category: Category) {
        navigateToCategory(category)
/*
        launch(Unconfined) {
            FileManager.getAllNonObservableSounds()
        }
        */
    }

    fun getSounds() : LiveData<ArrayList<Sound>> {
        /*
        FileManager.getAllSounds().observeForever(Observer {
            if(currentCategory == Category.allSoundsCategory.uuid){
                // Get all sounds
                soundsData.value = it
                Log.d("GetSounds", "1")
            }else{
                Log.d("GetSounds", "2")
                // Get the sounds of the category
                soundsData.value = Transformations.map(FileManager.getAllSounds()) {
                    it
                }.value
            }
        })
        liveData = FileManager.getAllSounds()
        */
        return mediatorLiveData
    }

    fun getCategories() : LiveData<ArrayList<Category>>{
        return Transformations.map(FileManager.getAllCategories()) {
            it.add(0, Category.allSoundsCategory)
            it
        }
    }

    fun navigateToCategory(category: Category){
        currentCategory = category.uuid
        FileManager.itemViewHolder.setTitle(category.name.value ?: "")
        closeDrawerData.value = true

        FileManager.itemViewHolder.currentCategory.value = category.uuid


        /*
        if(currentCategory == Category.allSoundsCategory.uuid){
            // Get all sounds
            mediatorLiveData.addSource(FileManager.getAllSounds(), Observer {
                mediatorLiveData.value = it
                Log.d("MediatorLiveData", "Sounds: ${it?.count()}")
            })
            Log.d("GetSounds", "1")
        }else{
            Log.d("GetSounds", "2")
            // Get the sounds of the category
            //liveData = FileManager.getSoundsByCategory(category.uuid)
        }
        */
    }

    fun drawerClosed(){
        closeDrawerData.value = false
    }
}