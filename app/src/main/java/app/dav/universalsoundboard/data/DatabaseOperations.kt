package app.dav.universalsoundboard.data

import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.data.Property
import app.dav.davandroidlibrary.data.TableObject
import java.util.*

object DatabaseOperations {
    // General methods
    suspend fun getObject(uuid: UUID) : TableObject?{
        return Dav.Database.getTableObject(uuid).await()
    }
    // End General methods

    // Sound methods
    fun createSound(uuid: UUID, name: String, soundUuid: String, categoryUuid: String){
        // Create the properties of the table object
        val nameProperty = Property()
        nameProperty.name = FileManager.soundTableNamePropertyName
        nameProperty.value = name

        val properties = arrayListOf(nameProperty)

        if(!categoryUuid.isEmpty()){
            val categoryProperty = Property()
            categoryProperty.name = FileManager.soundTableCategoryUuidPropertyName
            categoryProperty.value = categoryUuid
            properties.add(categoryProperty)
        }

        TableObject(uuid, FileManager.soundTableId, properties)
    }

    suspend fun getAllSounds() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.soundTableId, false).await()
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

        val properties = arrayListOf(nameProperty, iconProperty)
        TableObject(uuid, FileManager.categoryTableId, properties)
    }

    suspend fun getAllCategories() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.categoryTableId, false).await()
    }
    // End Category methods
}