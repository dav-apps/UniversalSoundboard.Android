package app.dav.universalsoundboard.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import app.dav.universalsoundboard.databinding.PlayingSoundListItemBinding
import app.dav.universalsoundboard.models.PlayingSound

class PlayingSoundListAdapter : ListAdapter<PlayingSound, PlayingSoundListAdapter.ViewHolder>(PlayingSoundDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlayingSoundListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: PlayingSoundListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlayingSound){
            binding.playingSound = item
        }
    }
}