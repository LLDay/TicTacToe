package com.example.tictactoe.logic.players

import android.content.Context
import android.widget.Toast
import com.example.tictactoe.GlobalParams
import com.example.tictactoe.GlobalParams.Companion.fieldSize
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets.UTF_8


class NetPlayer(private val context: Context, private val network: NearbyNetwork): LocalPlayer() {

    override fun move(x: Int, y: Int) {
        if (network.isConnected) {
            super.move(x, y)
            val m = y * fieldSize + x
            network.connectionsClient.sendPayload(
                    network.opponentEndpointId,
                    Payload.fromBytes(m.toString().toByteArray(UTF_8))
                )
        }
        else {
            Toast.makeText(context, "Player is not connected", Toast.LENGTH_SHORT).show()
        }
    }
}