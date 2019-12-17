package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.tictactoe.logic.APlayer
import com.example.tictactoe.logic.LocalPlayer
import com.example.tictactoe.logic.Session

class GameActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var session: Session
    lateinit var player1: APlayer
    lateinit var player2: APlayer


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

            }
        }

        session = Session(player1, player2)
        lifecycle.addObserver(session)
    }

    enum class SessionType {
        TWO_ON_ONE, TWO_ON_TWO
    }
}
