package app.dav.universalsoundboard.models

import android.media.Image
import java.util.*

class Sound(val uuid: UUID, val name: String, val category: Category?, val favourite: Boolean, val image: Image?)