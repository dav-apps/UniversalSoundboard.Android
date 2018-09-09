package app.dav.universalsoundboard.utilities

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

class ImageArrayAdapter(context: Context, objects: Array<Int>) : ArrayAdapter<Int>(context, android.R.layout.simple_spinner_item, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomRowView(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getCustomRowView(position)
    }

    private fun getCustomRowView(position: Int) : View{
        val imageView = ImageView(context)
        imageView.setImageResource(getItem(position))
        imageView.minimumHeight = 150
        imageView.minimumWidth = 150
        return imageView
    }
}