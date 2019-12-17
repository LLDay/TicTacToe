package com.example.tictactoe.logic

interface SessionListener {

    fun onMove(x: Int, y: Int, lastMove: Session.CellStates, status: Session.MoveStatus)

    fun onGameEnd(status: Session.EndGameStatus)

}