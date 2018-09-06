package app.dav.universalsoundboard.fragments

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Sound

/**
 * A fragment representing a list of Items.
 */
private const val TAG = "SoundFragment"

class SoundFragment : Fragment(), SoundListRecyclerViewAdapter.OnItemClickListener, SoundListRecyclerViewAdapter.OnItemLongClickListener {
    private var columnCount = 1
    private var clickListener: SoundListRecyclerViewAdapter.OnItemClickListener = this
    private var longClickListener: SoundListRecyclerViewAdapter.OnItemLongClickListener = this
    private val soundListRecyclerViewAdapter = SoundListRecyclerViewAdapter(clickListener, longClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        FileManager.getAllSounds().observe(this, Observer{
            Log.d(TAG, "Sounds list changed")
            soundListRecyclerViewAdapter.submitList(it)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sound, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = soundListRecyclerViewAdapter
            }
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onItemClicked(sound: Sound) {
        Log.d(TAG, "Item clicked: $sound")
    }

    override fun onItemLongClicked(sound: Sound) {
        Log.d(TAG, "Item long clicked: $sound")
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                SoundFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
