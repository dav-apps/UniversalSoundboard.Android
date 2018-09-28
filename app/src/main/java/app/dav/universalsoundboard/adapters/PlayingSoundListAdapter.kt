package app.dav.universalsoundboard.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.dav.universalsoundboard.databinding.PlayingSoundListItemBinding
import app.dav.universalsoundboard.models.PlayingSound

class PlayingSoundListAdapter(val clickListeners: PlayingSoundButtonClickListeners) : ListAdapter<PlayingSound, PlayingSoundListAdapter.ViewHolder>(PlayingSoundDiffCallback()) {

    interface PlayingSoundButtonClickListeners{
        fun skipPreviousButtonClicked(playingSound: PlayingSound)
        fun playPauseButtonClicked(playingSound: PlayingSound)
        fun skipNextButtonClicked(playingSound: PlayingSound)
        fun removeButtonClicked(playingSound: PlayingSound)
        fun menuButtonClicked(playingSound: PlayingSound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlayingSoundListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item,
                View.OnClickListener { clickListeners.skipPreviousButtonClicked(item) },
                View.OnClickListener { clickListeners.playPauseButtonClicked(item) },
                View.OnClickListener { clickListeners.skipNextButtonClicked(item) },
                View.OnClickListener { clickListeners.removeButtonClicked(item) },
                View.OnClickListener { clickListeners.menuButtonClicked(item) })
    }

    inner class ViewHolder(private val binding: PlayingSoundListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: PlayingSound,
                 skipPreviousClickListener: View.OnClickListener,
                 playPauseClickListener: View.OnClickListener,
                 skipNextClickListener: View.OnClickListener,
                 removeClickListener: View.OnClickListener,
                 menuClickListener: View.OnClickListener){
            binding.playingSound = item
            binding.skipPreviousClickListener = skipPreviousClickListener
            binding.playPauseClickListener = playPauseClickListener
            binding.skipNextClickListener = skipNextClickListener
            binding.removeClickListener = removeClickListener
            binding.menuClickListener = menuClickListener
        }
    }
}