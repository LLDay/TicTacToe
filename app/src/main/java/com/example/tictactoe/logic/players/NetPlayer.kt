package com.example.tictactoe.logic.players

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
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


class NetPlayer(private val context: Context): LocalPlayer() {

    private lateinit var connectionsClient: ConnectionsClient
    private val codeName: String = "name"

    private lateinit var opponentEndpointId: String
    private lateinit var opponentName: String

    private var isConnected = false

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
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                Log.i(globalTag, "onEndpointFound: endpoint found, connecting")
                connectionsClient.requestConnection(
                    codeName, endpointId,
                    connectionLifecycleCallback
                )
            }

            override fun onEndpointLost(endpointId: String) {
                Log.i(globalTag, "Endpoint lost")
            }
        }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                Log.i(globalTag, "onConnectionInitiated: accepting connection")
                connectionsClient.acceptConnection(endpointId, payloadCallback)
                opponentName = connectionInfo.endpointName
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                Log.d(globalTag, "Connection status: ${result.status.statusMessage}")
                if (result.status.isSuccess) {
                    Log.i(globalTag, "onConnectionResult: connection successful")
                    connectionsClient.stopDiscovery()
                    connectionsClient.stopAdvertising()
                    opponentEndpointId = endpointId
                    isConnected = true
                }
                else
                    Log.i(globalTag, "onConnectionResult: connection failed")
            }

            override fun onDisconnected(endpointId: String) {
                Log.i(globalTag, "onDisconnected: disconnected from the opponent")
                isConnected = false
            }
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onActivityStart() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            REQUIRED_PERMISSIONS.forEach {
                if (!hasPermissions(context, it)) {
                    requestPermissions(
                        context as GameActivity,
                        REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS
                    )
                }
            }
        }

//        startAdvertising()
        startDiscovering()
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStopActivity() {
        Log.d(globalTag, "Stop all endpoints")
        connectionsClient.stopAllEndpoints()
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED)
                return false
        return true
    }

    private fun startDiscovering() {
        Log.d(globalTag, "Start discovering")
        connectionsClient.startDiscovery(
            context.packageName, endpointDiscoveryCallback,
            DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        )
    }

    private fun startAdvertising() {
        Log.d(globalTag, "Start advertising")
        connectionsClient.startAdvertising(
            codeName, context.packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        )
    }


    override fun move(x: Int, y: Int) {
        if (isConnected) {
            super.move(x, y)
            val m = y * fieldSize + x
            connectionsClient.sendPayload(
                    opponentEndpointId,
                    Payload.fromBytes(m.toString().toByteArray(UTF_8))
                )
        }
        else {
            Toast.makeText(context, "Player is not connected", Toast.LENGTH_SHORT).show()
        }
    }

}