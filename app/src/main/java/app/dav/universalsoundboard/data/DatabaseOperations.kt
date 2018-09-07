package app.dav.universalsoundboard.data

import android.arch.lifecycle.LiveData
import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.data.Property
import app.dav.davandroidlibrary.data.TableObject
import java.util.*

object DatabaseOperations {
    // General methods
    fun getObject(uuid: UUID) : TableObject?{
        return Dav.Database.getTableObject(uuid)
    }
    // End General methods

    // Sound methods
    fun createSound(uuid: UUID, name: String, soundUuid: String, categoryUuid: String){
        // Create the properties of the table object
        val nameProperty = Property()
        nameProperty.name = FileManager.soundTableNamePropertyName
        nameProperty.value = name

        val properties = arrayListOf(nameProperty)
        TableObject(uuid, FileManager.soundTableId, properties)
    }

    fun getAllSounds() : LiveData<ArrayList<TableObject>>{
        return Dav.Database.getAllTableObjects(FileManager.soundTableId, false)
    }
    // End Sound methods

    // Category methods
    fun createCategory(uuid: UUID, name: String, icon: String){
        val nameProperty = Property()
        nameProperty.name = FileManager.categoryTableNamePropertyName
        nameProperty.value = name

        val iconProperty = Property()
        iconProperty.name = FileManager.categoryTableIconPropertyName
        iconProperty.value = icon

        val properties = arrayListOf<Property>(nameProperty, iconProperty)
        TableObject(uuid, FileManager.categoryTableId, properties)
    }

    fun getAllCategories() : LiveData<ArrayList<TableObject>>{
        return Dav.Database.getAllTableObjects(FileManager.categoryTableId, false)
    }
    // End Category methods
}