package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import com.example.tictactoe.database.DatabaseHelper
import com.example.tictactoe.logic.Session

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
            recreate()
        }

        // buttons
        findViewById<Button>(R.id.new_game_button).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("SessionType", GameActivity.SessionType.TWO_ON_ONE)
            intent.putExtra("Restore", false)
            startActivity(intent)
        }

        findViewById<Button>(R.id.continue_button).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("SessionType", GameActivity.SessionType.TWO_ON_ONE)
            intent.putExtra("Restore", true)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val db = DatabaseHelper(this)
        val info = db.getLastSession()

        val continueButton = findViewById<Button>(R.id.continue_button)
        continueButton.isEnabled = (info != null && info.status == Session.GameStatus.CONTINUES)

    }
}
