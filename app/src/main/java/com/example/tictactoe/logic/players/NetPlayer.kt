package com.example.tictactoe.logic.players

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.example.tictactoe.GameActivity
import com.example.tictactoe.GlobalParams.Companion.REQUEST_CODE_REQUIRED_PERMISSIONS
import com.example.tictactoe.GlobalParams.Companion.REQUIRED_PERMISSIONS
import com.example.tictactoe.GlobalParams.Companion.STRATEGY
import com.example.tictactoe.GlobalParams.Companion.fieldSize
import com.example.tictactoe.GlobalParams.Companion.globalTag
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.StandardCharsets.UTF_8


class NetPlayer(private val context: Context): APlayer() {

    private var connectionsClient: ConnectionsClient? = null
    private val codeName: String = "name"

    private var opponentEndpointId: String? = null
    private var opponentName: String? = null

    // Callbacks for receiving payloads
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val opponentMove = String(payload.asBytes()!!).toInt()
            move(opponentMove % fieldSize, opponentMove / fieldSize)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
        }
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(
                endpointId: String,
                info: DiscoveredEndpointInfo
            ) {
                Log.i(globalTag, "onEndpointFound: endpoint found, connecting")
                connectionsClient!!.requestConnection(
                    codeName,
                    endpointId,
                    connectionLifecycleCallback
                )
            }

            override fun onEndpointLost(endpointId: String) {}
        }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(
                endpointId: String,
                connectionInfo: ConnectionInfo
            ) {
                Log.i(globalTag, "onConnectionInitiated: accepting connection")
                connectionsClient!!.acceptConnection(endpointId, payloadCallback)
                opponentName = connectionInfo.endpointName
            }

            override fun onConnectionResult(
                endpointId: String,
                result: ConnectionResolution
            ) {
                if (result.status.isSuccess) {
                    Log.i(globalTag, "onConnectionResult: connection successful")
                    connectionsClient!!.stopDiscovery()
                    connectionsClient!!.stopAdvertising()
                    opponentEndpointId = endpointId
                } else {
                    Log.i(globalTag, "onConnectionResult: connection failed")
                }
            }

            override fun onDisconnected(endpointId: String) {
                Log.i(globalTag, "onDisconnected: disconnected from the opponent")
            }
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected fun onCreate() {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected fun onActivityStart() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            REQUIRED_PERMISSIONS.forEach {
                if (!hasPermissions(context, it)) {
                    requestPermissions(context as GameActivity,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS)
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStopActivity() {
        connectionsClient!!.stopAllEndpoints()
    }

    /** Returns true if the app was granted all the permissions. Otherwise, returns false.  */
    private fun hasPermissions(
        context: Context,
        vararg permissions: String
    ): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /** Finds an opponent to play the game with using Nearby Connections.  */
    fun findOpponent() {
        startAdvertising()
        startDiscovery()
    }

    private fun startDiscovery() { // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient!!.startDiscovery(
            context.packageName, endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        )
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us.  */
    private fun startAdvertising() { // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        connectionsClient!!.startAdvertising(
            codeName, context.packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        )
    }

    /** Sends the user's selection of rock, paper, or scissors to the opponent.  */
    private fun sendGameChoice(move: Int) {
        connectionsClient!!.sendPayload(
            opponentEndpointId!!, Payload.fromBytes(move.toString().toByteArray(UTF_8)))
    }

}