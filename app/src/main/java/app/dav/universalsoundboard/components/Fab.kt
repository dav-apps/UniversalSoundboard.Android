package app.dav.universalsoundboard.components

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import com.gordonwong.materialsheetfab.AnimatedFab


class Fab(context: Context, attrs: AttributeSet) : AnimatedFab, FloatingActionButton(context, attrs) {
    override fun show(translationX: Float, translationY: Float){}
}