package com.example.tictactoe.logic

import com.example.tictactoe.guielems.Field
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.Serializable

abstract class APlayer : Serializable {
    var channels = mutableListOf<Channel<Pair<Int, Int>>>()

    fun move(x: Int, y: Int) {
        GlobalScope.launch {
            channels.forEach { it.offer(Pair(x, y)) }
        }
    }

    fun addChannel(channel: Channel<Pair<Int, Int>>) {
        this.channels.add(channel)
    }

    open fun connectField(field: Field) {}
}