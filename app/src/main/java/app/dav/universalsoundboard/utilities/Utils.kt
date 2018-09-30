package app.dav.universalsoundboard.utilities

import java.util.*

class Utils {
    companion object {
        fun getUuidFromString(uuidString: String?) : UUID?{
            if(uuidString == null) return null
            try {
                return UUID.fromString(uuidString)
            }catch (e: Exception){
                return null
            }
        }
    }
}