package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.tictactoe.logic.LocalPlayer
import com.example.tictactoe.logic.Session

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)

        findViewById<Button>(R.id.new_game_button).setOnClickListener {
            val players = Bundle()
            val player = LocalPlayer()

            players.putSerializable("player_1", player)
            players.putSerializable("player_2", player)

            val intent = Intent(this, FieldActivity::class.java)
            intent.putExtra("players", players)
            startActivity(intent)
        }
    }
}
