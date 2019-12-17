package com.example.tictactoe.guielems

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.setMargins
import com.example.tictactoe.R


class Cell( context: Context) : ImageView(context) {
    init {
        val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT, 1f)
        lp.setMargins(2)

        val cellBackgroundId = if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            R.drawable.background_dark
        else R.drawable.background_light

        super.setImageResource(cellBackgroundId)
        super.setBackgroundResource(cellBackgroundId)
        super.setScaleType(ScaleType.FIT_XY)
        super.setLayoutParams(lp)
    }
}
