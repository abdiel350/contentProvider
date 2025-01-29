package es.ua.eps.sqlite

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewUser : AppCompatActivity() {

    private val contentUri = UserContract.CONTENT_URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)

        // Referencias a los campos de texto y botones
        val etUsername: EditText = findViewById(R.id.loginEditText)
        val etPassword: EditText = findViewById(R.id.passwordEditText)
        val etFullName: EditText = findViewById(R.id.usernameEditText)
        val btnSave: Button = findViewById(R.id.newUserButton)
        val btnBack: Button = findViewById(R.id.back)

        btnBack.setOnClickListener {
            val intent = Intent(this, UserManagement::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val fullName = etFullName.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty() && fullName.isNotEmpty()) {
                val values = ContentValues().apply {
                    put(UserContract.UserEntry.EMAIL, username)
                    put(UserContract.UserEntry.PASSWORD, password)
                    put(UserContract.UserEntry.NAME, fullName)
                }

                try {
                    val newUserUri: Uri? = contentResolver.insert(contentUri, values)
                    if (newUserUri != null) {
                        Toast.makeText(this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show()
                        etUsername.text.clear()
                        etPassword.text.clear()
                        etFullName.text.clear()
                    } else {
                        Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}