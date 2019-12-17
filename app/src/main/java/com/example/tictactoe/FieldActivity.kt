package com.example.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.example.tictactoe.guielems.Field
import com.example.tictactoe.logic.APlayer
import com.example.tictactoe.logic.Session

class FieldActivity : AppCompatActivity() {

    public lateinit var session: Session
    public lateinit var player1: APlayer
    public lateinit var player2: APlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.field)

        val bundle = intent.getBundleExtra("players")!!
        val player1 = bundle["player_1"] as APlayer
        val player2 = bundle["player_2"] as APlayer

        this.session = Session(player1, player2)
        this.player1 = player1
        this.player2 = player2
    }

}
