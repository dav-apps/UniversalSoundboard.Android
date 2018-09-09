package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.utilities.ImageArrayAdapter


class CreateCategoryDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val resourcesArray: Array<Int> = Category.getIconResourceIds()

        val layout = activity.layoutInflater.inflate(R.layout.dialog_create_category, null)
        val spinner = layout.findViewById<Spinner>(R.id.create_category_dialog_icon_spinner)

        val adapter = ImageArrayAdapter(context, resourcesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        return AlertDialog.Builder(activity).setView(layout)
                .setPositiveButton(R.string.create_category_dialog_positive_button_text, DialogInterface.OnClickListener{ dialog, which ->
                    Log.d("CreateCategory", "Hello World!")
                })
                .setNegativeButton(R.string.create_category_dialog_negative_button_text, null)
                .create()
    }
}