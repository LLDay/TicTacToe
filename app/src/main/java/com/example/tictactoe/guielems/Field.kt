package com.example.tictactoe.guielems

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.tictactoe.GameActivity
import com.example.tictactoe.GlobalParams.Companion.fieldSize
import com.example.tictactoe.GlobalParams.Companion.globalTag
import com.example.tictactoe.R
import com.example.tictactoe.logic.SessionListener
import com.example.tictactoe.logic.Session


class Field : Fragment(), SessionListener {

    val cells = mutableListOf<Cell>()

    //
    //  Lifecycle
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.field, container, false)
        buildGrid(view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity as GameActivity
        activity.session.addMoveListener(this)
        activity.player1.connectField(this)
        activity.player2.connectField(this)
    }

    //
    //  Grid builder
    //

    private fun buildGrid(currentView: View) {
        for (i in 0 until fieldSize) {
            val layout = getRow()

            layout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )

            (currentView as ViewGroup).addView(layout)
        }
    }

    private fun getRow(): LinearLayout {
        val layout = LinearLayout(context)
        layout.setBackgroundColor(Color.BLACK)

        for (i in 0 until fieldSize) {
            val cell = Cell(context)
            layout.addView(cell)
            cells.add(cell)
        }

        return layout
    }

    //
    //  SessionListener methods
    //

    override fun onMove(x: Int, y: Int, lastMove: Session.CellStates, status: Session.MoveStatus) {
        if (status == Session.MoveStatus.NO)
            return

        val imageView = cells[y * fieldSize + x]
        when (lastMove) {
            // This method calls from another thread
            Session.CellStates.O -> imageView.post { imageView.setImageResource(R.drawable.o) }
            Session.CellStates.X -> imageView.post { imageView.setImageResource(R.drawable.x) }
        }
    }

    override fun onGameEnd(status: Session.EndGameStatus) {
        //TODO(popup or fragment)
    }
}