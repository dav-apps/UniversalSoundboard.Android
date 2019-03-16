package app.dav.universalsoundboard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.adapters.SoundListAdapter
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.viewmodels.SoundViewModel

class SoundFragmentFavouritesTab : Fragment() {
    private val columnCount = 1
    private lateinit var viewModel: SoundViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SoundViewModel::class.java)
        viewModel.soundListAdapter = SoundListAdapter(context!!, parentFragment as SoundFragment, parentFragment as SoundFragment)

        FileManager.itemViewHolder.favouriteSounds.observe(this, Observer {
            if(it != null) viewModel.soundListAdapter?.submitList(it)
            viewModel.soundListAdapter?.notifyDataSetChanged()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sound_favourites_tab, container, false)

        if(view is RecyclerView){
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> androidx.recyclerview.widget.LinearLayoutManager(context)
                    else -> androidx.recyclerview.widget.GridLayoutManager(context, columnCount)
                }
                adapter = viewModel.soundListAdapter
            }
        }

        return view
    }
}