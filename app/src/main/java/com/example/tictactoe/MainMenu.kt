package com.example.tictactoe

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.example.tictactoe.logic.LocalPlayer

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // night mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.DarkTheme)
        else
            setTheme(R.style.AppTheme)

        // init
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_menu_layout)

        // night move
        val switch = findViewById<Switch>(R.id.switch_mode)

        switch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

//            val restartIntent = Intent(applicationContext, MainMenu::class.java)
//            startActivity(restartIntent)
//            finish()
            recreate()
        }

        // buttons
        findViewById<Button>(R.id.new_game_button).setOnClickListener {
            val players = Bundle()
            val player = LocalPlayer()

//            players.putSerializable("player_1", player)
//            players.putSerializable("player_2", player)

            val intent = Intent(this, GameActivity::class.java)
//            intent.putExtra("players", players)
            startActivity(intent)
        }



    }
}
