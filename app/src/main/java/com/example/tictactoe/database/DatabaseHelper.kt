package com.example.tictactoe.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tictactoe.logic.Session


class SessionInfo(var steps: List<Int>, var status: Session.GameStatus)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "session.db"
        private const val DB_VERSION= 1
        const val ID = "id"
        const val TABLE_NAME = "Session"
        const val STEPS = "Steps"
        const val STATUS = "Status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + STEPS + " TEXT, "
                    + STATUS + " INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addNewSession(info: SessionInfo): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(STEPS, info.steps.joinToString())
        values.put(STATUS, info.status.ordinal)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getLastSession(): SessionInfo? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $ID DESC LIMIT 1;", null)
        return if (cursor.moveToLast()) {
            val historyStr = cursor.getString(cursor.getColumnIndex(STEPS))
            val statusInt = cursor.getInt(cursor.getColumnIndex(STATUS))

            if (historyStr.isEmpty())
                return null

            val history = historyStr.split(", ").map { it.toInt() }
            val status = Session.GameStatus.values().find { it.ordinal == statusInt }!!

            db.close()
            cursor.close()
            SessionInfo(history, status)
        } else
            null
    }

}