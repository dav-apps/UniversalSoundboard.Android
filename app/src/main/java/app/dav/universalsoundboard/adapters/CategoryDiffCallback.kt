package app.dav.universalsoundboard.adapters

import android.support.v7.util.DiffUtil
import app.dav.universalsoundboard.models.Category

class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category?, newItem: Category?): Boolean {
        if(oldItem != null && newItem != null){
            return oldItem.uuid.equals(newItem.uuid)
        }
        return false
    }

    override fun areContentsTheSame(oldItem: Category?, newItem: Category?): Boolean {
        return oldItem == newItem
    }
}