package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SetCategoryDialogFragment : DialogFragment() {
    var sound: Sound? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog() ?: super.onCreateDialog(savedInstanceState)
    }

    private fun createDialog() : AlertDialog?{
        val s = sound ?: return null
        val allCategories = FileManager.itemViewHolder.categories.value ?: return null
        val categories = ArrayList<Category>()
        val selectedCategoriesBooleanArray = BooleanArray(allCategories.size - 1)

        // Create the categories lists
        var i = -1
        for(category in allCategories){
            if(++i == 0) continue
            categories.add(category)

            if(s.categories.any { c -> c.uuid == category.uuid}){
                // The sound belongs to the current category; add the category to the selected categories
                selectedCategoriesBooleanArray.set(i - 1, true)
            }
        }

        val categoriesNamesArray = Array<CharSequence>(categories.size) {
            categories[it].name
        }

        val alertDialog = AlertDialog.Builder(activity)
                .setTitle(getString(R.string.set_category_dialog_title, s.name))
                .setMultiChoiceItems(categoriesNamesArray, selectedCategoriesBooleanArray) { dialogInterface: DialogInterface, index: Int, isChecked: Boolean -> }
                .setPositiveButton(R.string.dialog_save) { dialog, which ->
                    // Get the selected categories
                    val selectedCategories = ArrayList<UUID>()
                    var i = 0
                    selectedCategoriesBooleanArray.forEach {
                        if(it){
                            selectedCategories.add(categories[i].uuid)
                        }
                        i++
                    }

                    // Save the new categories of the sound
                    GlobalScope.launch(Dispatchers.Main) {
                        FileManager.setCategoriesOfSound(s.uuid, selectedCategories)
                        FileManager.itemViewHolder.loadSounds()
                    }
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show()

        return alertDialog
    }
}