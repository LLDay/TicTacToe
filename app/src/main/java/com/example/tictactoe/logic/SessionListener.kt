package com.example.tictactoe.logic

interface SessionListener {
    fun onMove(x: Int, y: Int, status: Session.MoveStatus)

    fun onGameEnd(status: Session.MoveStatus)
}