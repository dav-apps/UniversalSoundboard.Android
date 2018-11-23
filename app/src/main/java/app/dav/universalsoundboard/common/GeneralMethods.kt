package app.dav.universalsoundboard.common

import app.dav.davandroidlibrary.DavEnvironment
import app.dav.davandroidlibrary.common.IGeneralMethods
import app.dav.universalsoundboard.data.FileManager

class GeneralMethods : IGeneralMethods {
    override fun isNetworkAvailable(): Boolean {
        return FileManager.isNetworkAvailable()
    }

    override fun getEnvironment() : DavEnvironment {
        return FileManager.environment
    }
}