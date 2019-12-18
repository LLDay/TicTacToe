package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import com.example.tictactoe.logic.players.APlayer
import com.example.tictactoe.logic.players.LocalPlayer
import com.example.tictactoe.logic.Session
import com.example.tictactoe.logic.players.NetPlayer
import com.example.tictactoe.logic.players.RemoteNetPlayer

class GameActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var session: Session
    lateinit var player1: APlayer
    lateinit var player2: APlayer

    enum class SessionType {
        TWO_ON_ONE, TWO_ON_TWO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.session_layout)

        when (intent.getSerializableExtra("SessionType") as SessionType) {
            SessionType.TWO_ON_ONE -> {
                player1 = LocalPlayer()
                player2 = player1
            }
            SessionType.TWO_ON_TWO -> {
                player1 = NetPlayer(this)
                player2 = RemoteNetPlayer()
            }
        }

        val restore = intent.getBooleanExtra("Restore", false)
        session = Session(this, restore)
        lifecycle.addObserver(session)
        lifecycle.addObserver(player1)
        lifecycle.addObserver(player2)
    }

    override fun onStart() {
        super.onStart()
        session.startSession(player1, player2)
    }
}
