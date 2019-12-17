package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.example.tictactoe.guielems.Field
import com.example.tictactoe.logic.APlayer
import com.example.tictactoe.logic.LocalPlayer
import com.example.tictactoe.logic.Session
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

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
        this.session = Session(player1, player2)
    }
}
