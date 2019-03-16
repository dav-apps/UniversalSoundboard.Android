package app.dav.universalsoundboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.databinding.FragmentSoundListItemBinding
import app.dav.universalsoundboard.models.Sound

class SoundListAdapter(
        val context: Context,
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
        holder.bind(context, View.OnClickListener{
            onItemClickListener.onItemClicked(item)
        }, View.OnLongClickListener {
            onItemLongClickListener.onItemLongClicked(item, holder.itemView)
            true
        }, item)
    }

    inner class ViewHolder(private val binding: FragmentSoundListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, onClickListener: View.OnClickListener, onLongClickListener: View.OnLongClickListener, item: Sound){
            binding.onClickListener = onClickListener
            binding.onLongClickListener = onLongClickListener
            binding.sound = item
            binding.showCategoryIcons = FileManager.itemViewHolder.showCategoriesOfSounds.value!!

            if(item.image != null){
                binding.root.findViewById<ImageView>(R.id.sound_list_item_image).setImageBitmap(item.image)
            }else{
                binding.root.findViewById<ImageView>(R.id.sound_list_item_image).setImageResource(R.drawable.ic_music_note)
            }

            val categoryIconsLinearLayout = binding.root.findViewById<LinearLayout>(R.id.sound_list_category_icons_linear_layout)
            categoryIconsLinearLayout.removeAllViews()

            FileManager.itemViewHolder.showCategoriesOfSounds.observeForever {
                binding.showCategoryIcons = it
            }

            for(category in item.categories){
                val icon = category.getIconImageResource()

                val imageView = ImageView(context)
                imageView.minimumWidth = 20
                imageView.minimumHeight = 20
                imageView.setImageResource(icon)

                categoryIconsLinearLayout.addView(imageView)
            }
        }
    }
}
