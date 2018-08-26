package app.dav.universalsoundboard.Fragments

import android.arch.lifecycle.LiveData
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.dav.universalsoundboard.Models.Sound
import app.dav.universalsoundboard.R
import kotlinx.android.synthetic.main.fragment_sound_list_item.view.*

private const val TAG = "RecyclerViewAdapter"

class SoundListRecyclerViewAdapter(
        private val mValues: ArrayList<Sound>?,
        private val onItemClickListener: OnItemClickListener,
        private val onItemLongClickListener: OnItemLongClickListener)
    : RecyclerView.Adapter<SoundListRecyclerViewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(sound: Sound)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(sound: Sound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_sound_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(mValues != null){
            val item = mValues[position]
            holder.nameTextView.text = item.name

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
    }

    override fun getItemCount(): Int = if(mValues != null) mValues.size else 0

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val imageView: ImageView = mView.sound_list_item_image
        val nameTextView: TextView = mView.sound_list_item_name
    }
}
