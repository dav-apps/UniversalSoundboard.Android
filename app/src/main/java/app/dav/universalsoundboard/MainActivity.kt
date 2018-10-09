package app.dav.universalsoundboard

import android.app.Activity
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.dav.davandroidlibrary.Dav
import app.dav.universalsoundboard.adapters.CategoryListAdapter
import app.dav.universalsoundboard.adapters.PlayingSoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.fragments.*
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.PlayingSound
import app.dav.universalsoundboard.services.MediaPlaybackService
import app.dav.universalsoundboard.services.NOTIFICATION_ID
import app.dav.universalsoundboard.viewmodels.MainViewModel
import com.gordonwong.materialsheetfab.MaterialSheetFab
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.category_list.*
import kotlinx.android.synthetic.main.playing_sound_bottom_sheet.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch


const val REQUEST_AUDIO_FILE_GET = 1

class MainActivity :
        AppCompatActivity(),
        CategoryListAdapter.OnItemClickListener,
        PlayingSoundListAdapter.PlayingSoundButtonClickListeners {
    private lateinit var viewModel: MainViewModel
    private var soundFragment: SoundFragment = SoundFragment.newInstance(1)
    private var settingsFragment: SettingsFragment = SettingsFragment.newInstance()
    private var currentFragment: CurrentFragment = CurrentFragment.SoundFragment
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

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
        viewModel.playingSoundListAdapter = PlayingSoundListAdapter(applicationContext, this)

        startService(Intent(applicationContext, MediaPlaybackService::class.java))

        bottomSheetBehavior = BottomSheetBehavior.from(playing_sound_bottom_sheet)
        val materialSheetFab = MaterialSheetFab(fab, fab_sheet, overlay, R.color.colorSecondary, R.color.colorPrimary)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        bottom_category_list_settings_item.setOnClickListener {
            drawer_layout.closeDrawers()
            showSettingsFragment()
        }

        fab_sheet_item_new_sound.setOnClickListener{
            materialSheetFab.hideSheet()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "audio/mpeg"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, REQUEST_AUDIO_FILE_GET)
            }
        }

        fab_sheet_item_new_category.setOnClickListener{
            CategoryDialogFragment().show(supportFragmentManager, "create_category")
            materialSheetFab.hideSheet()
        }

        // Bind the itemViewHolder properties to the UI
        FileManager.itemViewHolder.setTitle(resources.getString(R.string.all_sounds))
        FileManager.itemViewHolder.title.observe(this, Observer<String> { title -> supportActionBar?.title = title })

        Category.allSoundsCategory.name = resources.getString(R.string.all_sounds)
        category_list.layoutManager = LinearLayoutManager(this)
        category_list.adapter = viewModel.categoryListAdapter

        GlobalScope.launch(Dispatchers.Main) { FileManager.itemViewHolder.loadCategories() }
        FileManager.itemViewHolder.categories.observe(this, Observer {
            if(it != null) viewModel.categoryListAdapter?.submitList(it)
            viewModel.categoryListAdapter?.notifyDataSetChanged()
        })

        playing_sound_list.layoutManager = LinearLayoutManager(this)
        playing_sound_list.adapter = viewModel.playingSoundListAdapter

        GlobalScope.launch(Dispatchers.Main) { FileManager.itemViewHolder.loadPlayingSounds() }
        FileManager.itemViewHolder.playingSounds.observe(this, Observer {
            if(it == null) return@Observer

            if(it.count() == 0){
                hideBottomSheet()

                val handler = Handler()
                handler.postDelayed({
                    viewModel.playingSoundListAdapter?.submitList(it)
                    viewModel.playingSoundListAdapter?.notifyDataSetChanged()
                }, 100)
            }else{
                viewModel.playingSoundListAdapter?.submitList(it)
                viewModel.playingSoundListAdapter?.notifyDataSetChanged()

                val handler = Handler()
                handler.postDelayed({
                    showBottomSheet()
                }, 100)
            }
        })

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, soundFragment)
        transaction.add(R.id.fragment_container, settingsFragment)
        transaction.hide(settingsFragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if(currentFragment == CurrentFragment.SettingsFragment){
                // Show the SoundFragment
                showSoundFragment()
            }else if(FileManager.itemViewHolder.currentCategory.uuid != Category.allSoundsCategory.uuid){
                // App shows a category or the settings; navigate to All Sounds
                GlobalScope.launch(Dispatchers.Main) { FileManager.showCategory(Category.allSoundsCategory) }
            }else{
                // App shows All Sounds, close the app
                super.onBackPressed()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        FileManager.itemViewHolder.showCategoryIcons.observe(this, Observer {
            val editCategoryItem = toolbar.menu.findItem(R.id.action_edit_category)
            val deleteCategoryItem = toolbar.menu.findItem(R.id.action_delete_category)
            editCategoryItem.isVisible = it == true
            deleteCategoryItem.isVisible = it == true
        })

        FileManager.itemViewHolder.showPlayAllIcon.observe(this, Observer {
            val playAllItem = toolbar.menu.findItem(R.id.action_play_all)
            playAllItem.isVisible = it == true
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_edit_category -> {
                val fragment = CategoryDialogFragment()
                fragment.category = FileManager.itemViewHolder.currentCategory
                fragment.show(supportFragmentManager, "edit_category")
                true
            }
            R.id.action_delete_category -> {
                val fragment = DeleteCategoryDialogFragment()
                fragment.category = FileManager.itemViewHolder.currentCategory
                fragment.show(supportFragmentManager, "delete_category")
                true
            }
            R.id.action_play_all -> {
                val sounds = FileManager.itemViewHolder.sounds.value
                if(sounds != null){
                    GlobalScope.launch(Dispatchers.Main) {
                        // Create a PlayingSound
                        FileManager.addPlayingSound(null, sounds, 0, 0, false, 1.0)
                        FileManager.itemViewHolder.playingSounds.value?.last()?.playOrPause(applicationContext)
                    }
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

    override fun onDestroy() {
        super.onDestroy()

        stopService(Intent(applicationContext, MediaPlaybackService::class.java))

        // Disconnect all MediaBrowser of the Playing Sounds
        val playingSounds = FileManager.itemViewHolder.playingSounds.value ?: return

        for(playingSound in playingSounds){
            playingSound.disconnect()
        }

        // Remove the notification
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }

    override fun onItemClicked(category: Category) {
        drawer_layout.closeDrawers()
        showSoundFragment()

        GlobalScope.launch(Dispatchers.Main) {
            FileManager.showCategory(category)
        }
    }

    override fun skipPreviousButtonClicked(playingSound: PlayingSound) {
        playingSound.skipPrevious(applicationContext)
    }

    override fun playPauseButtonClicked(playingSound: PlayingSound) {
        playingSound.playOrPause(applicationContext)
    }

    override fun skipNextButtonClicked(playingSound: PlayingSound) {
        playingSound.skipNext(applicationContext)
    }

    override fun removeButtonClicked(playingSound: PlayingSound) {
        playingSound.stop(applicationContext)
        GlobalScope.launch(Dispatchers.Main) { FileManager.deletePlayingSound(playingSound.uuid) }
    }

    override fun menuButtonClicked(playingSound: PlayingSound, view: View) {
        val menu = PopupMenu(this, view)
        menu.inflate(R.menu.playing_sound_item_context_menu)
        menu.show()

        menu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.playing_sound_item_context_menu_repeat -> showSetRepetitionsDialog(playingSound)
            }
            true
        }
    }

    private fun showSetRepetitionsDialog(playingSound: PlayingSound){
        val fragmentManager = supportFragmentManager ?: return
        val fragment = SetRepetitionsDialogFragment()
        fragment.playingSound = playingSound
        fragment.show(fragmentManager, "playing_sound_item_dialog")
    }

    private fun showSoundFragment(){
        supportFragmentManager
                .beginTransaction()
                .hide(settingsFragment)
                .show(soundFragment)
                .commit()
        currentFragment = CurrentFragment.SoundFragment

        // Set the title
        FileManager.itemViewHolder.setTitle(FileManager.itemViewHolder.currentCategory.name)

        // Show the icons
        if(FileManager.itemViewHolder.currentCategory.uuid != Category.allSoundsCategory.uuid){
            // Show the Category icons
            FileManager.itemViewHolder.setShowCategoryIcons(true)
        }
        FileManager.itemViewHolder.setShowPlayAllIcon(true)
        showFab()
        showBottomSheet()
    }

    private fun showSettingsFragment(){
        supportFragmentManager
                .beginTransaction()
                .hide(soundFragment)
                .show(settingsFragment)
                .commit()
        currentFragment = CurrentFragment.SettingsFragment

        // Set the title
        FileManager.itemViewHolder.setTitle(getString(R.string.settings))

        // Hide the icons
        FileManager.itemViewHolder.setShowCategoryIcons(false)
        FileManager.itemViewHolder.setShowPlayAllIcon(false)
        hideFab()
        hideBottomSheet()
    }

    private fun showBottomSheet(){
        if(FileManager.itemViewHolder.playingSounds.value?.count() ?: 0 == 0) return

        if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN){
            // The bottom sheet was hidden
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val handler = Handler()
        handler.postDelayed({
            bottomSheetBehavior.isHideable = false
        }, 1)
    }

    private fun showFab(){
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.anchorId = R.id.playing_sound_bottom_sheet
        fab.layoutParams = layoutParams
        fab.show()
    }

    private fun hideBottomSheet(){
        if(FileManager.itemViewHolder.playingSounds.value?.count() ?: 0 < 0) return

        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun hideFab(){
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.anchorId = View.NO_ID
        fab.layoutParams = layoutParams
        fab.hide()
    }
}

enum class CurrentFragment(){
    SoundFragment(),
    SettingsFragment()
}