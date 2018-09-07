package app.dav.universalsoundboard.adapters

import android.support.v7.util.DiffUtil
import app.dav.universalsoundboard.models.Sound

class SoundDiffCallback : DiffUtil.ItemCallback<Sound>(){
    override fun areItemsTheSame(oldItem: Sound?, newItem: Sound?): Boolean {
        if(oldItem != null && newItem != null){
            return oldItem.uuid.equals(newItem.uuid)
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Sound?, newItem: Sound?): Boolean {
        return oldItem == newItem
    }
}