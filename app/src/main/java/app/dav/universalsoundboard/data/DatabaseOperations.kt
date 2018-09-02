package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log
import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.data.Property
import app.dav.davandroidlibrary.data.TableObject
import app.dav.davandroidlibrary.data.TableObjectEntity
import java.util.*

object DatabaseOperations {
    // Sound methods
    fun createSound(uuid: UUID, name: String, soundUuid: String, categoryUuid: String){
        // Create the properties of the table object
        val nameProperty = Property()
        nameProperty.name = FileManager.soundTableNamePropertyName
        nameProperty.value = name

        val properties = arrayListOf(nameProperty)
        TableObject(uuid = uuid, tableId = FileManager.soundTableId, properties = properties)
    }

    fun getAllSounds() : LiveData<ArrayList<TableObject>>{
        return Dav.Database.getAllTableObjects(FileManager.soundTableId, false)
    }
    // End sound methods
}

private class CreateSoundTask : AsyncTask<TableObject, Void, Unit>() {
    override fun doInBackground(vararg params: TableObject?) {
        val tableObject = params[0]
        if (tableObject != null) {

        }
    }
}