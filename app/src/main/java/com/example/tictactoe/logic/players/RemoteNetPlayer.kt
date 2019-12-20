package com.example.tictactoe.logic.players

class RemoteNetPlayer(net: NearbyNetwork): APlayer() {

    init {
        net.addReceiver(this)
    }
}