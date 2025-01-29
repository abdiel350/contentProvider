package es.ua.eps.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import es.ua.eps.sqlite.UserContract.UserEntry

class UserDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "usuarios.db"
        const val DATABASE_VERSION = 1

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${UserContract.TABLE_NAME} (" +
                    "${UserEntry.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "${UserEntry.NAME} TEXT NOT NULL," +
                    "${UserEntry.EMAIL} TEXT NOT NULL UNIQUE," +
                    "${UserEntry.PASSWORD} TEXT NOT NULL)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${UserContract.TABLE_NAME}"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}
