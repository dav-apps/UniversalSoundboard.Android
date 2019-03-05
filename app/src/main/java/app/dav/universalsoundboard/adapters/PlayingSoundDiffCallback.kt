package app.dav.universalsoundboard.adapters

import androidx.recyclerview.widget.DiffUtil
import app.dav.universalsoundboard.models.PlayingSound

class PlayingSoundDiffCallback : DiffUtil.ItemCallback<PlayingSound>() {
    override fun areItemsTheSame(oldItem: PlayingSound, newItem: PlayingSound): Boolean {
        return oldItem.uuid == newItem.uuid
    }

    override fun areContentsTheSame(oldItem: PlayingSound, newItem: PlayingSound): Boolean {
        return oldItem == newItem
    }
}