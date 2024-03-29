package com.example.tictactoe.logic.players

import android.view.View
import com.example.tictactoe.GlobalParams
import com.example.tictactoe.guielems.Field


open class LocalPlayer : APlayer() {
    
    override fun connectField(field: Field) {
        for (i in 0 until field.cells.size) {
            field.cells[i].setOnClickListener(CellClickListener(this, i))
        }
    }

    private inner class CellClickListener(private val player: APlayer, absPos: Int)
        : View.OnClickListener {
        private val posX = absPos % GlobalParams.fieldSize
        private val posY = absPos / GlobalParams.fieldSize

        override fun onClick(v: View?) {
            player.move(posX, posY)
        }
    }

}

