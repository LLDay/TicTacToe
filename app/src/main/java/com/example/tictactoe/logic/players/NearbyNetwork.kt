package com.example.tictactoe.logic.players

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.tictactoe.GameActivity
import com.example.tictactoe.GlobalParams
import com.example.tictactoe.GlobalParams.Companion.globalTag
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*

open class NearbyNetwork(private val context: Context): LifecycleObserver {

    private val codeName: String = "name"

    var isConnected = false
    private set

    lateinit var connectionsClient: ConnectionsClient
    private set

    lateinit var opponentEndpointId: String
    private set

    protected lateinit var opponentName: String

    private val playerList = mutableListOf<APlayer>()

    fun addReceiver(player: APlayer) {
        playerList.add(player)
    }

    // Callbacks for receiving payloads
    val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val opponentMove = String(payload.asBytes()!!).toInt()
            playerList.forEach {
                it.move(opponentMove % GlobalParams.fieldSize, opponentMove / GlobalParams.fieldSize)
            }
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
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                isConnected = false
            }
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun onCreate() {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onActivityStart() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            GlobalParams.REQUIRED_PERMISSIONS.forEach {
                if (!hasPermissions(context, it)) {
                    ActivityCompat.requestPermissions(
                        context as GameActivity,
                        GlobalParams.REQUIRED_PERMISSIONS,
                        GlobalParams.REQUEST_CODE_REQUIRED_PERMISSIONS
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
            DiscoveryOptions.Builder().setStrategy(GlobalParams.STRATEGY).build()
        )
    }

    private fun startAdvertising() {
        Log.d(globalTag, "Start advertising")
        connectionsClient.startAdvertising(
            codeName, context.packageName, connectionLifecycleCallback,
            AdvertisingOptions.Builder().setStrategy(GlobalParams.STRATEGY).build()
        )
    }
}