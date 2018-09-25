package app.dav.universalsoundboard

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.dav.davandroidlibrary.Dav
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.fragments.CategoryDialogFragment
import app.dav.universalsoundboard.fragments.DeleteCategoryDialogFragment
import app.dav.universalsoundboard.fragments.SoundFragment
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.services.BUNDLE_SOUNDS_KEY
import app.dav.universalsoundboard.services.CUSTOM_ACTION_PLAY
import app.dav.universalsoundboard.services.MediaPlaybackService
import app.dav.universalsoundboard.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.category_list.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch


const val REQUEST_AUDIO_FILE_GET = 1

class MainActivity : AppCompatActivity(), CategoryListAdapter.OnItemClickListener {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        init()
    }

    private fun init(){
        Dav.init(this, FileManager.getDavDataPath(filesDir.path).path + "/")
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.categoryListAdapter = CategoryListAdapter(this)

        startService(Intent(applicationContext, MediaPlaybackService::class.java))

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        fab_menu_new_sound.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "audio/mpeg"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_AUDIO_FILE_GET)
            }
            fab_menu.close(true)
        }

        fab_menu_new_category.setOnClickListener{
            fab_menu.close(true)
            CategoryDialogFragment().show(fragmentManager, "create_category")
        }

        // Bind the itemViewHolder properties to the UI
        FileManager.itemViewHolder.setTitle(resources.getString(R.string.all_sounds))
        FileManager.itemViewHolder.title.observe(this, Observer<String> { title -> supportActionBar?.setTitle(title) })

        Category.allSoundsCategory.name = resources.getString(R.string.all_sounds)
        category_list.layoutManager = LinearLayoutManager(this)
        category_list.adapter = viewModel.categoryListAdapter

        GlobalScope.launch(Dispatchers.Main) { FileManager.itemViewHolder.loadCategories() }
        FileManager.itemViewHolder.categories.observe(this, Observer {
            if(it != null) viewModel.categoryListAdapter?.submitList(it)
            viewModel.categoryListAdapter?.notifyDataSetChanged()
        })

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, SoundFragment.newInstance(1)).commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        setCategoryIconsVisibility()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit_category -> {
                GlobalScope.launch(Dispatchers.Main) {
                    val fragment = CategoryDialogFragment()
                    fragment.category = FileManager.getCategory(FileManager.itemViewHolder.currentCategory)
                    fragment.show(fragmentManager, "edit_category")
                }
                true
            }
            R.id.action_delete_category -> {
                GlobalScope.launch(Dispatchers.Main) {
                    val fragment = DeleteCategoryDialogFragment()
                    fragment.category = FileManager.getCategory(FileManager.itemViewHolder.currentCategory)
                    fragment.show(fragmentManager, "delete_category")
                }
                true
            }
            R.id.action_play_all -> {
                val sounds = FileManager.itemViewHolder.sounds.value
                if(sounds != null){
                    val bundle = Bundle()
                    val soundsList = ArrayList<String>()
                    for(sound in sounds){
                        soundsList.add(sound.uuid.toString())
                    }
                    bundle.putStringArrayList(BUNDLE_SOUNDS_KEY, soundsList)
                    FileManager.itemViewHolder.mediaController.transportControls.sendCustomAction(CUSTOM_ACTION_PLAY, bundle)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_AUDIO_FILE_GET && resultCode == Activity.RESULT_OK){
            val fileUri: Uri? = data?.data
            val clipData = data?.clipData

            GlobalScope.launch(Dispatchers.Main) {
                if(fileUri != null){
                    // One file selected
                    viewModel.copySoundFile(fileUri, application.contentResolver, cacheDir)
                }else if(clipData != null){
                    // Multiple files selected
                    for(i in 0 until clipData.itemCount){
                        viewModel.copySoundFile(clipData.getItemAt(i).uri, application.contentResolver, cacheDir)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClicked(category: Category) {
        drawer_layout.closeDrawers()

        GlobalScope.launch(Dispatchers.Main) {
            FileManager.showCategory(category)
            setCategoryIconsVisibility()
        }
    }

    private fun setCategoryIconsVisibility(){
        val editCategoryItem = toolbar.menu.findItem(R.id.action_edit_category)
        val deleteCategoryItem = toolbar.menu.findItem(R.id.action_delete_category)
        val isVisible = FileManager.itemViewHolder.currentCategory != Category.allSoundsCategory.uuid
        editCategoryItem.isVisible = isVisible
        deleteCategoryItem.isVisible = isVisible
    }
}