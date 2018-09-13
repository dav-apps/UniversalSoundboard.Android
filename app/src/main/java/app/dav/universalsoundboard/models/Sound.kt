package app.dav.universalsoundboard.models

import android.media.Image
import java.util.*

class Sound(val uuid: UUID, var name: String, var category: Category?, var favourite: Boolean, var image: Image?)