package app.dav.universalsoundboard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import app.dav.universalsoundboard.R
import app.dav.universalsoundboard.data.FileManager
import app.dav.universalsoundboard.models.Category
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.launch

class DeleteCategoryDialogFragment : DialogFragment() {
    var category: Category? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return createDialog()
    }

    private fun createDialog() : AlertDialog{
        return AlertDialog.Builder(activity)
                .setTitle(getString(R.string.delete_category_dialog_title, category?.name))
                .setMessage(R.string.delete_category_dialog_message)
                .setPositiveButton(R.string.delete_category_dialog_positive_button_text) { dialog, which ->
                    val c = category
                    if(c != null){
                        GlobalScope.launch(Dispatchers.Main) {
                            FileManager.deleteCategory(c.uuid)
                            FileManager.showCategory(Category.allSoundsCategory)
                        }
                    }
                }
                .setNegativeButton(R.string.dialog_negative_button, null)
                .show()
    }
}