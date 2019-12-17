package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.session_layout)

//        val bundle = intent.getBundleExtra("players")!!
//        val player1 = bundle["player_1"] as APlayer
//        val player2 = bundle["player_2"] as APlayer
        this.player1 = LocalPlayer() //TODO(must be send within bundle!)
        this.player2 = player1
        session = Session(player1, player2)
        lifecycle.addObserver(session)
    }

}
