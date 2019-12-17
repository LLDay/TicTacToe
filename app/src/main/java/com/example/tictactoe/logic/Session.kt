package com.example.tictactoe.logic

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.tictactoe.GlobalParams.Companion.globalTag
import com.example.tictactoe.GlobalParams.Companion.fieldSize
import com.example.tictactoe.GlobalParams.Companion.winNumber
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch


class Session(player1: APlayer, player2: APlayer) : LifecycleObserver {

    //
    // Properties and fields
    //

    private val channel1 = Channel<Pair<Int, Int>>()
    private val channel2 = Channel<Pair<Int, Int>>()

    private val moveListeners = mutableListOf<SessionListener>()
    private var xMoves = true

    private val cells = Array(fieldSize) { Array(fieldSize) { CellStates.SPACE } }

    enum class CellStates { X, O, SPACE }
    enum class MoveStatus { OK, NO }
    enum class EndGameStatus { DRAW, X_WON, O_WON}

    val lastMove: CellStates
        get() = if (xMoves) CellStates.O else CellStates.X

    val currentMove: CellStates
        get() = if (xMoves) CellStates.X else CellStates.O

    var job: Job


    //
    //  Lifecycle
    //

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun saveStateToDB() {
        Log.d(globalTag, "Load from db")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun restoreStateFromDB() {
        Log.d(globalTag, "Load to db")
    }


    //
    //  Global logic
    //


    init {
        player1.addChannel(channel1)
        player2.addChannel(channel2)

        job = GlobalScope.launch {
            process()
        }
    }

    private suspend fun process() {
        while(job.isActive) {
            val coords = if (xMoves) channel1.receive() else channel2.receive()
            moveTo(coords.first, coords.second)
            tryEndGame(coords.first, coords.second)
        }
    }

    //
    //  User API
    //

    fun getCell(x: Int, y: Int): CellStates {
        if (x !in 0 until fieldSize || y !in 0 until fieldSize)
            throw ArrayIndexOutOfBoundsException()

        return cells[x][y]
    }

    fun addMoveListener(listener: SessionListener) {
        moveListeners.add(listener)
    }

    //
    //  Logic Details
    //

    private fun moveTo(x: Int, y: Int) {
        if (cells[x][y] != CellStates.SPACE) {
            Log.d(globalTag, "Cannot move at $x:$y")
            moveListeners.forEach { it.onMove(x, y, currentMove, MoveStatus.NO) }
            return
        }

        cells[x][y] = currentMove
        xMoves = !xMoves

        Log.d(globalTag, "Move $lastMove at $x:$y")
        moveListeners.forEach { it.onMove(x, y, lastMove, MoveStatus.OK) }
    }

    private fun tryEndGame(x: Int, y: Int) {
        var endStatus: EndGameStatus?
        endStatus = null

        if (isDraw()) {
            Log.d(globalTag, "Draw")
            endStatus = EndGameStatus.DRAW
        }

        if (isSomeoneWins(x, y)) {
            Log.d(globalTag, "$lastMove won")
            endStatus = when (lastMove) {
                CellStates.X -> EndGameStatus.X_WON
                CellStates.O -> EndGameStatus.O_WON
                else -> throw IllegalStateException("Current move cannot be undefined")
            }
        }

        if (endStatus != null) {
            job.cancel()
            moveListeners.forEach { it.onGameEnd(endStatus) }
        }
    }


    private fun isSomeoneWins(x: Int, y: Int): Boolean {
        val winNumber = winNumber
        val fieldSize = fieldSize

        //horizontal
        var counter = 0
        for (i in 0 until fieldSize) {
            counter = if (cells[i][y] == lastMove) counter + 1 else 0
            if (counter == winNumber)
                return true
        }

        //vertical
        counter = 0
        for (j in 0 until fieldSize) {
            counter = if (cells[x][j] == lastMove) counter + 1 else 0
            if (counter == winNumber)
                return true
        }

        //left diagonal
        counter = 0
        for (i in 1 - fieldSize until fieldSize) {
            if (x + i !in 0 until fieldSize || y + i !in 0 until fieldSize)
                continue

            counter = if (cells[x + i][y + i] == lastMove) counter + 1 else 0
            if (counter == winNumber)
                return true
        }

        //right diagonal
        counter = 0
        for (i in 1 - fieldSize until fieldSize) {
            if (x + i !in 0 until fieldSize || y - i !in 0 until fieldSize)
                continue

            counter = if (cells[x + i][y - i] == lastMove) counter + 1 else 0
            if (counter == winNumber)
                return true
        }
        return false
    }


    private fun isDraw(): Boolean {
        for (i in 0 until fieldSize)
            for (j in 0 until fieldSize)
                if (cells[i][j] == CellStates.SPACE)
                    return false

        return true
    }
}