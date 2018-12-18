package app.dav.universalsoundboard.data

import android.util.Log
import app.dav.davandroidlibrary.Dav
import app.dav.davandroidlibrary.models.Property
import app.dav.davandroidlibrary.models.TableObject
import app.dav.universalsoundboard.models.Sound
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

object DatabaseOperations {
    // General methods
    suspend fun getObject(uuid: UUID) : TableObject? = withContext(Dispatchers.IO) {
        Dav.Database.getTableObject(uuid)
    }
    // End General methods

    // Sound methods
    suspend fun createSound(uuid: UUID, name: String, soundUuid: String, categoryUuid: String) = withContext(Dispatchers.IO) {
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

    suspend fun getAllSounds() : ArrayList<TableObject> = withContext(Dispatchers.IO) {
        Dav.Database.getAllTableObjects(FileManager.soundTableId, false)
    }

    suspend fun updateSound(uuid: UUID, name: String?, favourite: String?, soundUuid: String?, imageUuid: String?, categoryUuid: String?) = withContext(Dispatchers.IO) {
        // Get the sound table object
        val soundTableObject = Dav.Database.getTableObject(uuid) ?: return@withContext

        if(soundTableObject.tableId != FileManager.soundTableId) return@withContext

        if(!name.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableNamePropertyName, name)
        if(!favourite.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableFavouritePropertyName, favourite)
        if(!soundUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableSoundUuidPropertyName, soundUuid)
        if(!imageUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableImageUuidPropertyName, imageUuid)
        if(!categoryUuid.isNullOrEmpty()) soundTableObject.setPropertyValue(FileManager.soundTableCategoryUuidPropertyName, categoryUuid)
    }

    suspend fun deleteSound(uuid: UUID) = withContext(Dispatchers.IO) {
        val soundTableObject = Dav.Database.getTableObject(uuid) ?: return@withContext
        if(soundTableObject.tableId != FileManager.soundTableId) return@withContext

        // Delete the sound file and the image file table objects
        val soundFileUuidString = soundTableObject.getPropertyValue(FileManager.soundTableSoundUuidPropertyName)
        val imageFileUuidString = soundTableObject.getPropertyValue(FileManager.soundTableImageUuidPropertyName)

        if(soundFileUuidString != null){
            val soundFileUuid = UUID.fromString(soundFileUuidString)
            val soundFileTableObject = Dav.Database.getTableObject(soundFileUuid)
            if(soundFileTableObject != null) soundFileTableObject.delete()
        }

        if(imageFileUuidString != null){
            val imageFileUuuid = UUID.fromString(imageFileUuidString)
            val imageFileTableObject = Dav.Database.getTableObject(imageFileUuuid)
            if(imageFileTableObject != null) imageFileTableObject.delete()
        }

        // Delete the sound itself
        soundTableObject.delete()
    }
    // End Sound methods

    // SoundFile methods
    suspend fun createSoundFile(uuid: UUID, audioFile: File) = withContext(Dispatchers.IO) {
        TableObject.create(uuid, FileManager.soundFileTableId, audioFile)
    }

    suspend fun getAllSoundFiles() : ArrayList<TableObject> = withContext(Dispatchers.IO) {
        Dav.Database.getAllTableObjects(FileManager.soundFileTableId, false)
    }
    // End SoundFile methods

    // ImageFile methods
    suspend fun createImageFile(uuid: UUID, imageFile: File) = withContext(Dispatchers.IO) {
        TableObject.create(uuid, FileManager.imageFileTableId, imageFile)
    }

    suspend fun updateImageFile(uuid: UUID, imageFile: File) = withContext(Dispatchers.IO) {
        val imageFileTableObject = Dav.Database.getTableObject(uuid) ?: return@withContext
        if(imageFileTableObject.tableId != FileManager.imageFileTableId) return@withContext

        imageFileTableObject.setFile(imageFile)
    }
    // End ImageFile methods

    // Category methods
    suspend fun createCategory(uuid: UUID, name: String, icon: String) = withContext(Dispatchers.IO) {
        val nameProperty = Property()
        nameProperty.name = FileManager.categoryTableNamePropertyName
        nameProperty.value = name

        val iconProperty = Property()
        iconProperty.name = FileManager.categoryTableIconPropertyName
        iconProperty.value = icon

        val properties = arrayListOf(nameProperty, iconProperty)
        TableObject.create(uuid, FileManager.categoryTableId, properties)
    }

    suspend fun getAllCategories() : ArrayList<TableObject> = withContext(Dispatchers.IO) {
        Dav.Database.getAllTableObjects(FileManager.categoryTableId, false)
    }

    suspend fun updateCategory(uuid: UUID, name: String?, icon: String?) = withContext(Dispatchers.IO) {
        val categoryTableObject = getObject(uuid) ?: return@withContext
        if(categoryTableObject.tableId != FileManager.categoryTableId) return@withContext

        if(!name.isNullOrEmpty()) categoryTableObject.setPropertyValue(FileManager.categoryTableNamePropertyName, name)
        if(!icon.isNullOrEmpty()) categoryTableObject.setPropertyValue(FileManager.categoryTableIconPropertyName, icon)
    }

    suspend fun deleteCategory(uuid: UUID) = withContext(Dispatchers.IO) {
        val categoryTableObject = getObject(uuid) ?: return@withContext
        if(categoryTableObject.tableId != FileManager.categoryTableId) return@withContext
        categoryTableObject.delete()
    }
    // End Category methods

    // PlayingSound methods
    suspend fun createPlayingSound(uuid: UUID, sounds: ArrayList<Sound>, current: Int, repetitions: Int, randomly: Boolean, volume: Double) = withContext(Dispatchers.IO) {
        val properties = ArrayList<Property>()

        val soundIds = ArrayList<String>()
        for(sound in sounds)
            soundIds.add(sound.uuid.toString())

        val soundIdsString = soundIds.joinToString(",")
        properties.add(Property(0, FileManager.playingSoundTableSoundIdsPropertyName, soundIdsString))
        properties.add(Property(0, FileManager.playingSoundTableCurrentPropertyName, current.toString()))
        properties.add(Property(0, FileManager.playingSoundTableRepetitionsPropertyName, repetitions.toString()))
        properties.add(Property(0, FileManager.playingSoundTableRandomlyPropertyName, randomly.toString()))
        properties.add(Property(0, FileManager.playingSoundTableVolumePropertyName, volume.toString()))

        TableObject.create(uuid, FileManager.playingSoundTableId, properties)
    }

    suspend fun getAllPlayingSounds() : ArrayList<TableObject> = withContext(Dispatchers.IO) {
        Dav.Database.getAllTableObjects(FileManager.playingSoundTableId, false)
    }

    suspend fun updatePlayingSound(uuid: UUID, sounds: ArrayList<Sound>?, current: Int?, repetitions: Int?, randomly: Boolean?, volume: Double?) = withContext(Dispatchers.IO) {
        val playingSoundTableObject = DatabaseOperations.getObject(uuid) ?: return@withContext
        if(playingSoundTableObject.tableId != FileManager.playingSoundTableId) return@withContext

        if(sounds != null){
            val soundIds = ArrayList<String>()
            for(sound in sounds)
                soundIds.add(sound.uuid.toString())

            val soundIdsString = soundIds.joinToString(",")
            playingSoundTableObject.setPropertyValue(FileManager.playingSoundTableSoundIdsPropertyName, soundIdsString)
        }
        if(current != null) playingSoundTableObject.setPropertyValue(FileManager.playingSoundTableCurrentPropertyName, current.toString())
        if(repetitions != null) playingSoundTableObject.setPropertyValue(FileManager.playingSoundTableRepetitionsPropertyName, repetitions.toString())
        if(randomly != null) playingSoundTableObject.setPropertyValue(FileManager.playingSoundTableRandomlyPropertyName, randomly.toString())
        if(volume != null) playingSoundTableObject.setPropertyValue(FileManager.playingSoundTableVolumePropertyName, volume.toString())
    }

    suspend fun deletePlayingSound(uuid: UUID) = withContext(Dispatchers.IO) {
        val playingSoundTableObject = getObject(uuid) ?: return@withContext
        if(playingSoundTableObject.tableId != FileManager.playingSoundTableId) return@withContext
        playingSoundTableObject.delete()
    }
    // End PlayingSound methods
}