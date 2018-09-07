package app.dav.universalsoundboard.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.models.Sound
import app.dav.universalsoundboard.databinding.FragmentSoundListItemBinding

class SoundListRecyclerViewAdapter(
        private val onItemClickListener: OnItemClickListener,
        private val onItemLongClickListener: OnItemLongClickListener)
    : ListAdapter<Sound, SoundListRecyclerViewAdapter.ViewHolder>(SoundDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClicked(sound: Sound)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(sound: Sound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentSoundListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(View.OnClickListener{
            onItemClickListener.onItemClicked(item)
        }, View.OnLongClickListener {
            onItemLongClickListener.onItemLongClicked(item)
            true
        }, item)
    }

    inner class ViewHolder(private val binding: FragmentSoundListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onClickListener: View.OnClickListener, onLongClickListener: View.OnLongClickListener, item: Sound){
            binding.onClickListener = onClickListener
            binding.onLongClickListener = onLongClickListener
            binding.sound = item
        }
    }
}
