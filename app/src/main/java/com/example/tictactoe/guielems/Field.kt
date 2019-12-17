package com.example.tictactoe.guielems

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.tictactoe.FieldActivity
import com.example.tictactoe.GlobalParams.Companion.fieldSize
import com.example.tictactoe.R
import com.example.tictactoe.logic.SessionListener
import com.example.tictactoe.logic.Session


class Field(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs), SessionListener {

    public val cells = mutableListOf<Cell>()
    private val session: Session


    private fun getRow(): LinearLayout {
        val layout = LinearLayout(context)
        layout.setBackgroundColor(Color.BLACK)
        layout.orientation = HORIZONTAL

        for (i in 0 until fieldSize) {
            val cell = Cell(context)
            layout.addView(cell)
            cells.add(cell)
        }
        return layout
    }

    init {
        // add listener of session events
        val activity = context as FieldActivity
        session = activity.session
        session.addMoveListener(this)

        // build a cell grid
        super.setOrientation(VERTICAL)
        for (i in 0 until fieldSize) {
            val layout = getRow()
            layout.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1f)

            super.addView(layout)
        }

        // connect players to field
        activity.player1.connectField(this)
        activity.player2.connectField(this)
    }

    override fun onMove(x: Int, y: Int, status: Session.MoveStatus) {
        val imageView = cells[y * fieldSize + x]
        when (session.lastMove) {
            // This method calls from another thread
            Session.CellStates.O -> imageView.post { imageView.setImageResource(R.drawable.o) }
            Session.CellStates.X -> imageView.post { imageView.setImageResource(R.drawable.x) }
        }
    }

    override fun onGameEnd(status: Session.MoveStatus) {
        //TODO(popup or fragment)
    }




}