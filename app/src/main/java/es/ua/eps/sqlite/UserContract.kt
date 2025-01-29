package es.ua.eps.sqlite

import android.net.Uri
import android.provider.BaseColumns

object UserContract {
    const val TABLE_NAME = "users"

    const val CONTENT_AUTHORITY = "es.ua.eps.sqlite.provider"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$CONTENT_AUTHORITY")

    val CONTENT_URI: Uri = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME)

    object UserEntry : BaseColumns {
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}
