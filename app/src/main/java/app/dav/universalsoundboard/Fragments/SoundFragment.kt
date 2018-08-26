package app.dav.universalsoundboard.Fragments

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
import app.dav.universalsoundboard.DataAccess.FileManager
import app.dav.universalsoundboard.Models.Sound
import app.dav.universalsoundboard.R
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [SoundFragment.OnListFragmentInteractionListener] interface.
 */
private const val TAG = "SoundFragment"

class SoundFragment : Fragment(), SoundListRecyclerViewAdapter.OnItemClickListener, SoundListRecyclerViewAdapter.OnItemLongClickListener {
    private var columnCount = 1
    private var clickListener: SoundListRecyclerViewAdapter.OnItemClickListener = this
    private var longClickListener: SoundListRecyclerViewAdapter.OnItemLongClickListener = this

    init {
        FileManager.itemViewHolder.soundListRecyclerViewAdapter = SoundListRecyclerViewAdapter(FileManager.itemViewHolder.sounds, clickListener, longClickListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sound_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = FileManager.itemViewHolder.soundListRecyclerViewAdapter
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
