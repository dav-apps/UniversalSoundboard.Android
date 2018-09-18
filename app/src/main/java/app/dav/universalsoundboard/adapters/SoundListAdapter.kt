package app.dav.universalsoundboard.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.databinding.FragmentSoundListItemBinding
import app.dav.universalsoundboard.models.Sound

class SoundListAdapter(
        private val onItemClickListener: OnItemClickListener,
        private val onItemLongClickListener: OnItemLongClickListener)
    : ListAdapter<Sound, SoundListAdapter.ViewHolder>(SoundDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClicked(sound: Sound)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(sound: Sound, item: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentSoundListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(View.OnClickListener{
            onItemClickListener.onItemClicked(item)
        }, View.OnLongClickListener {
            onItemLongClickListener.onItemLongClicked(item, holder.itemView)
            true
        }, item)
    }

    inner class ViewHolder(private val binding: FragmentSoundListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onClickListener: View.OnClickListener, onLongClickListener: View.OnLongClickListener, item: Sound){
            binding.onClickListener = onClickListener
            binding.onLongClickListener = onLongClickListener
            binding.sound = item

            if(item.image != null) binding.root.findViewById<ImageView>(R.id.sound_list_item_image).setImageBitmap(item.image)
        }
    }
}
