package com.kavin.noteit.noteit

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.widget.Toast

import com.kavin.noteit.noteit.data_model.Visitor
import org.jetbrains.anko.db.*
import java.util.*


class AnkoDatabaseHelper(con: Context) : ManagedSQLiteOpenHelper(con, "visitorDatabase", null, 1){

    val context = con

    companion object {

        private var instance: AnkoDatabaseHelper? = null
        @Synchronized
        fun Instance(con: Context): AnkoDatabaseHelper {
            if (instance == null) {
                instance = AnkoDatabaseHelper(con.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db!!.createTable(Visitor.TABLE_NAME, true,
                    Visitor.MOBILE to SqlType.create("TEXT PRIMARY KEY"),
                    Visitor.DATE to TEXT,
                    Visitor.NAME to TEXT,
                    Visitor.PHOTO to TEXT,
                    Visitor.PROBLEMS to TEXT
            )
        } catch (e: Exception) {
            Toast.makeText(context,e.toString(), Toast.LENGTH_LONG)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db!!.dropTable(Visitor.TABLE_NAME, true)
        } catch (e: Exception) {
            Toast.makeText(context,e.toString(), Toast.LENGTH_LONG)
        }

    }

}
