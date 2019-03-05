package app.dav.universalsoundboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.dav.universalsoundboard.databinding.CategoryListItemBinding
import app.dav.universalsoundboard.models.Category

class CategoryListAdapter(
        private val onItemClickListener: OnItemClickListener)
    : ListAdapter<Category, CategoryListAdapter.ViewHolder>(CategoryDiffCallback()){

    interface OnItemClickListener {
        fun onItemClicked(category: Category)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(View.OnClickListener {
            onItemClickListener.onItemClicked(item)
        }, item)
    }

    inner class ViewHolder(private val binding: CategoryListItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(onClickListener: View.OnClickListener, item: Category){
            binding.onClickListener = onClickListener
            binding.category = item
        }
    }
}