package app.dav.universalsoundboard.models

import java.util.*

class PlayingSound(val uuid: UUID,
                   val currentSound: Int,
                   val sounds: ArrayList<Sound>,
                    /*val mediaBrowser: MediaBrowser, */
                   val repetitions: Int,
                   val randomly: Boolean,
                   val volume: Double){

    fun getCurrentSoundObject() : Sound{
        return sounds[currentSound]
    }
}