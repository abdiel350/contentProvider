package es.ua.eps.sqlite

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListUser : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private val userUri = UserContract.CONTENT_URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_user)

        // RecyclerView
        recyclerView = findViewById(R.id.usersrecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtener usuarios desde ContentProvider
        val cursor: Cursor? = contentResolver.query(userUri, null, null, null, null)

        if (cursor != null) {
            // Asignar el adaptador que recibe el Cursor
            val adapter = UserAdapter(this, cursor)
            recyclerView.adapter = adapter
        } else {
            Log.d("ListUser", "Error al cargar los usuarios desde ContentProvider")
        }

        // Botón "BACK"
        val btnBack = findViewById<Button>(R.id.back)
        btnBack.setOnClickListener {
            val intent = Intent(this, UserManagement::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cerrar el cursor si aún está abierto
        val cursor = (recyclerView.adapter as UserAdapter).cursor
        if (cursor != null && !cursor.isClosed) {
            cursor.close()
        }
    }
}
