package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.utilities.ImageArrayAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class CategoryDialogFragment : DialogFragment() {
    var category: Category? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog() ?: super.onCreateDialog(savedInstanceState)
    }

    private fun createDialog() : AlertDialog?{
        val layout = activity?.layoutInflater?.inflate(R.layout.dialog_category, null) ?: return null
        val spinner = layout.findViewById<Spinner>(R.id.create_category_dialog_icon_spinner)
        val nameEditText = layout.findViewById<EditText>(R.id.create_category_dialog_name_edittext)
        val titleTextView = layout.findViewById<TextView>(R.id.create_category_dialog_title_textview)

        // Create the spinner list
        val resourcesArray: Array<Int> = Category.getIconResourceIds()
        val adapter = ImageArrayAdapter(context!!, resourcesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val c = category
        if(c != null){
            val i = Category.convertStringToIconResourceId(c.icon)
            val position = resourcesArray.indexOf(i)

            spinner.setSelection(position)

            // Set the title
            titleTextView.setText(R.string.edit_category_dialog_title)

            // Set the editText
            nameEditText.setText(c.name)
        }else{
            // Select a random icon
            spinner.setSelection(Random().nextInt(resourcesArray.size))
        }

        val alertDialog =  AlertDialog.Builder(activity)
                .setView(layout)
                .setPositiveButton(R.string.dialog_save) { dialog, which ->
                    if(c != null){
                        // Update the current category
                        GlobalScope.launch(Dispatchers.Main) {
                            FileManager.updateCategory(c.uuid, nameEditText.text.toString(), Category.convertIconResourceIdToString(spinner.selectedItem as Int))
                        }
                    }else{
                        // Create a new category
                        GlobalScope.launch(Dispatchers.Main) {
                            val newCategory = FileManager.addCategory(null, nameEditText.text.toString(), Category.convertIconResourceIdToString(spinner.selectedItem as Int))
                            if(newCategory != null) FileManager.showCategory(newCategory)
                        }
                    }
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .create()
        alertDialog.show()

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = category != null

        // Disable the positive button when the name is too short
        nameEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                positiveButton.isEnabled = nameEditText.text.length > 1
            }
        })

        return alertDialog
    }
}