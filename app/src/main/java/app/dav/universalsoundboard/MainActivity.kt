package app.dav.universalsoundboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import app.dav.davandroidlibrary.Dav
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.fragments.SoundFragment
import app.dav.universalsoundboard.viewmodels.MainViewModel
import app.dav.universalsoundboard.viewmodels.SoundViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.category_list.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        Dav.init(this)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        fab_menu_new_sound.setOnClickListener{view ->
            FileManager.addSound(null, "Hello World", null)
            fab_menu.close(true)
        }

        fab_menu_new_category.setOnClickListener{view ->
            FileManager.addCategory(null, "TestCategory", "bla")
            fab_menu.close(true)
        }

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, SoundFragment.newInstance(1)).commit()

        // Bind the itemViewHolder properties to the UI
        FileManager.itemViewHolder.title.observe(this, Observer<String> { title -> supportActionBar?.setTitle(title) })

        category_list.layoutManager = LinearLayoutManager(this)
        category_list.adapter = viewModel.categoryListAdapter

        viewModel.getCategories().observe(this, Observer{
            Log.d(TAG, "Categories list changed!")
            viewModel.categoryListAdapter.submitList(it)
        })
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }
}