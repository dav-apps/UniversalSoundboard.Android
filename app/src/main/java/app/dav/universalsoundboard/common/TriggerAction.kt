package app.dav.universalsoundboard.common

import app.dav.davandroidlibrary.common.ITriggerAction
import app.dav.davandroidlibrary.models.TableObject
import app.dav.universalsoundboard.data.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TriggerAction : ITriggerAction {
    override fun updateAllOfTable(tableId: Int) {
        updateView(tableId)
    }

    override fun updateTableObject(tableObject: TableObject, fileDownloaded: Boolean) {

    }

    override fun deleteTableObject(tableObject: TableObject) {
        updateView(tableObject.tableId)
    }

    private fun updateView(tableId: Int){
        if(tableId == FileManager.imageFileTableId || tableId == FileManager.soundFileTableId){
            // Update the sounds
            GlobalScope.launch(Dispatchers.Main) {
                FileManager.itemViewHolder.allSoundsChanged = true
                FileManager.itemViewHolder.loadSounds()
            }
        }else if(tableId == FileManager.categoryTableId){
            // Update the categories
            GlobalScope.launch(Dispatchers.Main) { FileManager.itemViewHolder.loadCategories() }
        }else if(tableId == FileManager.playingSoundTableId){
            // Update the playing sounds
            GlobalScope.launch(Dispatchers.Main) { FileManager.itemViewHolder.loadPlayingSounds() }
        }
    }
}