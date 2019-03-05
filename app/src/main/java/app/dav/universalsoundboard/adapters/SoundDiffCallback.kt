package app.dav.universalsoundboard.adapters

import androidx.recyclerview.widget.DiffUtil
import app.dav.universalsoundboard.models.Sound

class SoundDiffCallback : DiffUtil.ItemCallback<Sound>(){
    override fun areItemsTheSame(oldItem: Sound, newItem: Sound): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: Sound, newItem: Sound): Boolean {
        return oldItem == newItem
    }
}