package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import com.example.tictactoe.logic.Session
import com.example.tictactoe.logic.players.*

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
                val net = NearbyNetwork(this)
                lifecycle.addObserver(net)

                player1 = NetPlayer(this, net)
                player2 = RemoteNetPlayer(net)

                net.addReceiver(player1)
            }
            /*
            FUTURE
            SessionType.ONE_AI -> {
                player1 = LocalPlayer()
                player2 = AiPlayer()
            }
            SessoinType.AI_AI -> {
                player1 = AiPlayer()
                player2 = AiPlayer()
            }
             */
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
