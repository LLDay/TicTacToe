package com.example.tictactoe.logic

import com.example.tictactoe.guielems.Field
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

abstract class APlayer : SessionListener {
    protected val channels = mutableListOf<Channel<Pair<Int, Int>>>()

    fun move(x: Int, y: Int) =
        GlobalScope.launch {
            channels.forEach { it.offer(Pair(x, y)) }
        }

    fun addChannel(channel: Channel<Pair<Int, Int>>) {
        channels.add(channel)
    }

    open fun connectField(field: Field) {
    }

    override fun onMove(x: Int, y: Int, lastMove: Session.CellStates, status: Session.MoveStatus) {
    }

    override fun onGameEnd(status: Session.GameStatus) {

    }
}