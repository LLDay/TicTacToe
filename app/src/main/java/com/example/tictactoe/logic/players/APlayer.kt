package com.example.tictactoe.logic.players

import androidx.lifecycle.LifecycleObserver
import com.example.tictactoe.guielems.Field
import com.example.tictactoe.logic.Session
import com.example.tictactoe.logic.SessionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

abstract class APlayer : SessionListener, LifecycleObserver {
    private val channels = mutableListOf<Channel<Pair<Int, Int>>>()

    open fun move(x: Int, y: Int) {
        GlobalScope.launch {
            channels.forEach { it.offer(Pair(x, y)) }
        }
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