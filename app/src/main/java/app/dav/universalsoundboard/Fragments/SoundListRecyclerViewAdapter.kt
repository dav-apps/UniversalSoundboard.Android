package app.dav.universalsoundboard.Fragments

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.dav.universalsoundboard.R


import app.dav.universalsoundboard.Fragments.SoundFragment.OnListFragmentInteractionListener
import app.dav.universalsoundboard.Fragments.dummy.DummyContent.DummyItem

import kotlinx.android.synthetic.main.fragment_sound_list_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
private const val TAG = "RecyclerViewAdapter"

class SoundListRecyclerViewAdapter(
        private val mValues: List<DummyItem>,
        private val onItemClickListener: OnItemClickListener,
        private val onItemLongClickListener: OnItemLongClickListener
        )
    : RecyclerView.Adapter<SoundListRecyclerViewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(sound: DummyItem)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(sound: DummyItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_sound_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mContentView.text = item.content

        with(holder.mView) {
            setOnClickListener {
                onItemClickListener.onItemClicked(item)
            }
            setOnLongClickListener {
                onItemLongClickListener.onItemLongClicked(item)
                true
            }
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
