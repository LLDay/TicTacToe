package com.example.tictactoe.guielems

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setMargins
import com.example.tictactoe.R

class Cell( context: Context?) : ImageView(context) {
    init {
        val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f)
        lp.setMargins(2)

        super.setImageResource(R.drawable.background_light)
        super.setBackgroundResource(R.drawable.background_light)
        super.setScaleType(ScaleType.FIT_XY)
        super.setLayoutParams(lp)
    }
}
