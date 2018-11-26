package app.dav.universalsoundboard.common

import app.dav.davandroidlibrary.common.IRetrieveConstants
import app.dav.universalsoundboard.data.FileManager

class RetrieveConstants : IRetrieveConstants {
    override fun getDataPath(): String {
        val filePath = FileManager.itemViewHolder.mainActivity?.filesDir?.path ?: return ""
        return FileManager.getDavDataPath(filePath).path + "/"
    }

    override fun getApiKey(): String {
        return FileManager.apiKey
    }

    override fun getAppId(): Int {
        return FileManager.appId
    }

    override fun getTableIds(): ArrayList<Int> {
        return arrayListOf(
                FileManager.categoryTableId,
                FileManager.soundTableId,
                FileManager.soundFileTableId,
                FileManager.playingSoundTableId,
                FileManager.imageFileTableId)
    }
}