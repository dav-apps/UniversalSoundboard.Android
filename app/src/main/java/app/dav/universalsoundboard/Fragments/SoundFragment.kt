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
import app.dav.universalsoundboard.R

import app.dav.universalsoundboard.Fragments.dummy.DummyContent
import app.dav.universalsoundboard.Fragments.dummy.DummyContent.DummyItem
import android.widget.Toast
import app.dav.universalsoundboard.MainActivity
import app.dav.universalsoundboard.Models.Sound


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [SoundFragment.OnListFragmentInteractionListener] interface.
 */
private const val TAG = "SoundFragment"

class SoundFragment : Fragment(), SoundListRecyclerViewAdapter.OnItemClickListener, SoundListRecyclerViewAdapter.OnItemLongClickListener {
    // TODO: Customize parameters
    private var columnCount = 1

    private var clickListener: SoundListRecyclerViewAdapter.OnItemClickListener = this
    private var longClickListener: SoundListRecyclerViewAdapter.OnItemLongClickListener = this

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
                adapter = SoundListRecyclerViewAdapter(DummyContent.ITEMS, clickListener, longClickListener)
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

    override fun onItemClicked(sound: DummyItem) {
        Log.d(TAG, "Item clicked: $sound")
    }

    override fun onItemLongClicked(sound: DummyItem) {
        Log.d(TAG, "Item long clicked: $sound")
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyItem?)
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
