package com.example.tictactoe

import android.Manifest
import com.google.android.gms.nearby.connection.Strategy

class GlobalParams {
    companion object{
        const val globalTag = "TicTacToeTag"
        const val fieldSize = 10
        const val winNumber = 5

        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
        val STRATEGY = Strategy.P2P_STAR
    }
}