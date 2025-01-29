package es.ua.eps.sqlite

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import android.content.UriMatcher
import es.ua.eps.sqlite.UserContract.UserEntry

class UserProvider : ContentProvider() {

    companion object {
        private const val USERS = 100
        private const val USER_NAME = 101

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(UserContract.CONTENT_AUTHORITY, UserContract.TABLE_NAME, USERS)
            addURI(UserContract.CONTENT_AUTHORITY, "${UserContract.TABLE_NAME}/*", USER_NAME)
        }
    }

    private lateinit var dbHelper: UserDbHelper
    private lateinit var database: SQLiteDatabase

    override fun onCreate(): Boolean {
        dbHelper = UserDbHelper(context!!)
        database = dbHelper.writableDatabase
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)
        return when (match) {
            USERS -> database.query(
                UserContract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder
            )
            USER_NAME -> {
                val email = uri.lastPathSegment
                database.query(
                    UserContract.TABLE_NAME, projection, "${UserEntry.EMAIL} = ?", arrayOf(email), null, null, sortOrder
                )
            }
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)
        return when (match) {
            USERS -> {
                val id = database.insert(UserContract.TABLE_NAME, null, values)
                if (id > 0) Uri.withAppendedPath(uri, id.toString()) else throw SQLException("Error al insertar: $uri")
            }
            else -> throw IllegalArgumentException("URI inválida para inserción: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            USER_NAME -> {
                val email = uri.lastPathSegment
                Log.d("UserProvider", "Actualizando usuario con email: $email")
                database.update(UserContract.TABLE_NAME, values, "${UserEntry.EMAIL} = ?", arrayOf(email))
            }
            else -> throw IllegalArgumentException("URI desconocida para actualización: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            USER_NAME -> {
                val email = uri.lastPathSegment
                database.delete(UserContract.TABLE_NAME, "${UserEntry.EMAIL} = ?", arrayOf(email))
            }
            else -> throw IllegalArgumentException("URI desconocida para eliminación: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            USERS -> "vnd.android.cursor.dir/${UserContract.CONTENT_AUTHORITY}.${UserContract.TABLE_NAME}"
            USER_NAME -> "vnd.android.cursor.item/${UserContract.CONTENT_AUTHORITY}.${UserContract.TABLE_NAME}"
            else -> throw IllegalArgumentException("URI desconocida: $uri")
        }
    }
}
