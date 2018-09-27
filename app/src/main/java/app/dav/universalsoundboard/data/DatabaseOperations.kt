package app.dav.universalsoundboard.data

import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.models.Property
import app.dav.davandroidlibrary.models.TableObject
import java.io.File
import java.util.*

object DatabaseOperations {
    // General methods
    suspend fun getObject(uuid: UUID) : TableObject?{
        return Dav.Database.getTableObject(uuid).await()
    }
    // End General methods

    // Sound methods
    suspend fun createSound(uuid: UUID, name: String, soundUuid: String, categoryUuid: String){
        // Create the properties of the table object
        val nameProperty = Property()
        nameProperty.name = FileManager.soundTableNamePropertyName
        nameProperty.value = name

        val properties = arrayListOf(nameProperty)

        if(!soundUuid.isEmpty()){
            val soundFileProperty = Property()
            soundFileProperty.name = FileManager.soundTableSoundUuidPropertyName
            soundFileProperty.value = soundUuid
            properties.add(soundFileProperty)
        }

        if(!categoryUuid.isEmpty()){
            val categoryProperty = Property()
            categoryProperty.name = FileManager.soundTableCategoryUuidPropertyName
            categoryProperty.value = categoryUuid
            properties.add(categoryProperty)
        }

        TableObject.create(uuid, FileManager.soundTableId, properties)
    }

    suspend fun getAllSounds() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.soundTableId, false).await()
    }

    suspend fun updateSound(uuid: UUID, name: String?, favourite: String?, soundUuid: String?, imageUuid: String?, categoryUuid: String?){
        // Get the sound table object
        val soundTableObject = Dav.Database.getTableObject(uuid).await()

        if(soundTableObject == null) return
        if(soundTableObject.tableId != FileManager.soundTableId) return

        if(!name.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableNamePropertyName, name!!)
        if(!favourite.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableFavouritePropertyName, favourite!!)
        if(!soundUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableSoundUuidPropertyName, soundUuid!!)
        if(!imageUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableImageUuidPropertyName, imageUuid!!)
        if(!categoryUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableCategoryUuidPropertyName, categoryUuid!!)
    }

    suspend fun deleteSound(uuid: UUID){
        val soundTableObject = Dav.Database.getTableObject(uuid).await() ?: return
        if(soundTableObject.tableId != FileManager.soundTableId) return

        // Delete the sound file and the image file table objects
        val soundFileUuidString = soundTableObject.getPropertyValue(FileManager.soundTableSoundUuidPropertyName)
        val imageFileUuidString = soundTableObject.getPropertyValue(FileManager.soundTableImageUuidPropertyName)

        if(soundFileUuidString != null){
            val soundFileUuid = UUID.fromString(soundFileUuidString)
            val soundFileTableObject = Dav.Database.getTableObject(soundFileUuid).await()
            if(soundFileTableObject != null) soundFileTableObject.delete()
        }

        if(imageFileUuidString != null){
            val imageFileUuuid = UUID.fromString(imageFileUuidString)
            val imageFileTableObject = Dav.Database.getTableObject(imageFileUuuid).await()
            if(imageFileTableObject != null) imageFileTableObject.delete()
        }

        // Delete the sound itself
        soundTableObject.delete()
    }
    // End Sound methods

    // SoundFile methods
    suspend fun createSoundFile(uuid: UUID, audioFile: File){
        TableObject.create(uuid, FileManager.soundFileTableId, audioFile)
    }

    suspend fun getAllSoundFiles() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.soundFileTableId, false).await()
    }
    // End SoundFile methods

    // ImageFile methods
    suspend fun createImageFile(uuid: UUID, imageFile: File){
        TableObject.create(uuid, FileManager.imageFileTableId, imageFile)
    }

    suspend fun updateImageFile(uuid: UUID, imageFile: File){
        val imageFileTableObject = Dav.Database.getTableObject(uuid).await() ?: return
        if(imageFileTableObject.tableId != FileManager.imageFileTableId) return

        imageFileTableObject.setFile(imageFile)
    }
    // End ImageFile methods

    // Category methods
    suspend fun createCategory(uuid: UUID, name: String, icon: String){
        val nameProperty = Property()
        nameProperty.name = FileManager.categoryTableNamePropertyName
        nameProperty.value = name

        val iconProperty = Property()
        iconProperty.name = FileManager.categoryTableIconPropertyName
        iconProperty.value = icon

        val properties = arrayListOf(nameProperty, iconProperty)
        TableObject.create(uuid, FileManager.categoryTableId, properties)
    }

    suspend fun updateCategory(uuid: UUID, name: String?, icon: String?){
        val categoryTableObject = getObject(uuid) ?: return
        if(categoryTableObject.tableId != FileManager.categoryTableId) return

        if(!name.isNullOrEmpty()) categoryTableObject.setPropertyValue(FileManager.categoryTableNamePropertyName, name!!)
        if(!icon.isNullOrEmpty()) categoryTableObject.setPropertyValue(FileManager.categoryTableIconPropertyName, icon!!)
    }

    suspend fun deleteCategory(uuid: UUID){
        val categoryTableObject = getObject(uuid) ?: return
        if(categoryTableObject.tableId != FileManager.categoryTableId) return
        categoryTableObject.delete()
    }

    suspend fun getAllCategories() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.categoryTableId, false).await()
    }
    // End Category methods

    // PlayingSound methods
    suspend fun createPlayingSound(uuid: UUID, soundIds: ArrayList<String>, current: Int, repetitions: Int, randomly: Boolean, volume: Double){
        val properties = ArrayList<Property>()

        // SoundIds Property
        var soundIdsString = ""
        for(id in soundIds){
            soundIdsString += id
        }
        properties.add(Property(0, FileManager.playingSoundTableSoundIdsPropertyName, soundIdsString))
        properties.add(Property(0, FileManager.playingSoundTableCurrentPropertyName, current.toString()))
        properties.add(Property(0, FileManager.playingSoundTableRepetitionsPropertyName, repetitions.toString()))
        properties.add(Property(0, FileManager.playingSoundTableRandomlyPropertyName, randomly.toString()))
        properties.add(Property(0, FileManager.playingSoundTableVolumePropertyName, volume.toString()))

        TableObject.create(uuid, FileManager.playingSoundTableId, properties)
    }

    suspend fun getAllPlayingSounds() : ArrayList<TableObject>{
        return Dav.Database.getAllTableObjects(FileManager.playingSoundTableId, false).await()
    }
    // End PlayingSound methods
}