package app.dav.universalsoundboard.models

import android.graphics.Bitmap
import java.io.File
import java.util.*

class Sound(val uuid: UUID, var name: String, var category: Category?, var favourite: Boolean, var audioFile: File?, var image: Bitmap?)