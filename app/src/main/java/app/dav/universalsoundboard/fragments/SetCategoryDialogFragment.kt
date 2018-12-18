package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Button
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SetCategoryDialogFragment : DialogFragment() {
    var sound: Sound? = null
    var selectedItem = -1
    var firstSelectedItem = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog() ?: super.onCreateDialog(savedInstanceState)
    }

    private fun createDialog() : AlertDialog?{
        if(sound == null) return null
        val categories = FileManager.itemViewHolder.categories.value ?: return null
        val categoriesArrayList = ArrayList<Category>()

        var i = -1
        for(c in categories){
            categoriesArrayList.add(c)

            if(c.uuid == sound?.category?.uuid) {
                firstSelectedItem = i
                selectedItem = i
            }
            i++
        }

        categoriesArrayList.removeAt(0)
        val categoriesNamesArray = Array<CharSequence>(categoriesArrayList.size) {
            categoriesArrayList[it].name
        }
        var positiveButton: Button? = null

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.set_category_dialog_title))
                .setSingleChoiceItems(categoriesNamesArray, selectedItem) { dialog, which ->
                    selectedItem = which

                    positiveButton?.isEnabled = selectedItem != firstSelectedItem
                }
                .setPositiveButton(R.string.set_category_dialog_positive_button_text) { dialog, which ->
                    if(selectedItem == -1) return@setPositiveButton
                    val s = sound ?: return@setPositiveButton
                    val selectedCategory = categoriesArrayList.get(selectedItem)

                    GlobalScope.launch(Dispatchers.Main) {
                        FileManager.setCategoryOfSound(s.uuid, selectedCategory.uuid)
                        FileManager.itemViewHolder.loadSounds()
                    }
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show()

        positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false

        return alertDialog
    }
}