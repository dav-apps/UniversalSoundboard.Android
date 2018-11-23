package app.dav.universalsoundboard.common

import app.dav.davandroidlibrary.common.ILocalDataSettings
import app.dav.universalsoundboard.data.FileManager

class LocalDataSettings : ILocalDataSettings {
    override fun setBooleanValue(key: String, value: Boolean) {
        FileManager.setBooleanValue(key, value)
    }

    override fun getBooleanValue(key: String, defaultValue: Boolean): Boolean {
        return FileManager.getBooleanValue(key, defaultValue)
    }

    override fun setStringValue(key: String, value: String) {
        FileManager.setStringValue(key, value)
    }

    override fun getStringValue(key: String, defaultValue: String): String {
        return FileManager.getStringValue(key, defaultValue)
    }

    override fun setLongValue(key: String, value: Long) {
        FileManager.setLongValue(key, value)
    }

    override fun getLongValue(key: String, defaultValue: Long): Long {
        return FileManager.getLongValue(key, defaultValue)
    }

    override fun setIntValue(key: String, value: Int) {
        FileManager.setIntValue(key, value)
    }

    override fun getIntValue(key: String, defaultValue: Int): Int {
        return FileManager.getIntValue(key, defaultValue)
    }
}