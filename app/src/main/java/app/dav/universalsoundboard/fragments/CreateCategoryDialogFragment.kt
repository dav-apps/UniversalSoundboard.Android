package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Spinner
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.utilities.ImageArrayAdapter
import kotlinx.coroutines.experimental.launch
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class CreateCategoryDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    fun createDialog() : AlertDialog{
        val layout = activity.layoutInflater.inflate(R.layout.dialog_create_category, null)
        val spinner = layout.findViewById<Spinner>(R.id.create_category_dialog_icon_spinner)
        val nameEditText = layout.findViewById<EditText>(R.id.create_category_dialog_name_edittext)

        // Create the spinner list
        val resourcesArray: Array<Int> = Category.getIconResourceIds()
        val adapter = ImageArrayAdapter(context, resourcesArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Select a random icon
        spinner.setSelection(Random().nextInt(resourcesArray.size))

        val alertDialog =  AlertDialog.Builder(activity).setView(layout)
                .setPositiveButton(R.string.create_category_dialog_positive_button_text, DialogInterface.OnClickListener{ dialog, which ->
                    launch { FileManager.addCategory(null, nameEditText.text.toString(), Category.convertIconResourceIdToString(spinner.selectedItem as Int)) }
                })
                .setNegativeButton(R.string.create_category_dialog_negative_button_text, null)
                .create()
        alertDialog.show()

        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false

        // Disable the positive button when no name was entered
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