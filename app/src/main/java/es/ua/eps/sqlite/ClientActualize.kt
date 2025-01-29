package es.ua.eps.sqlite

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ClientActualize : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var fullNameEditText: EditText

    private val contentUri = UserContract.CONTENT_URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_udapte_user)

        usernameEditText = findViewById(R.id.loginEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        fullNameEditText = findViewById(R.id.usernameEditText)

        // Obtener datos del intent
        val originalUsername = intent.getStringExtra("username") ?: return
        val originalFullName = intent.getStringExtra("fullName") ?: ""
        val originalPassword = intent.getStringExtra("password") ?: ""

        // Cargar datos en los campos
        usernameEditText.setText(originalUsername)
        fullNameEditText.setText(originalFullName)
        passwordEditText.setText(originalPassword)

        val btnUpdateUser = findViewById<Button>(R.id.useractualizar)
        btnUpdateUser.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()
            val newPassword = passwordEditText.text.toString().trim()
            val newFullName = fullNameEditText.text.toString().trim()

            if (newUsername.isBlank() || newPassword.isBlank() || newFullName.isBlank()) {
                Toast.makeText(this, "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contentValues = ContentValues().apply {
                put(UserContract.UserEntry.NAME, newFullName)
                put(UserContract.UserEntry.EMAIL, newUsername)
                put(UserContract.UserEntry.PASSWORD, newPassword)
            }

            if (originalUsername != newUsername) {
                // Si cambia el username, eliminar y volver a insertar
                val deleteUri = Uri.withAppendedPath(contentUri, originalUsername)
                val rowsDeleted = contentResolver.delete(deleteUri, null, null)

                if (rowsDeleted > 0) {
                    val newUserUri = contentResolver.insert(contentUri, contentValues)
                    if (newUserUri != null) {
                        Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al crear el nuevo usuario", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al eliminar el usuario antiguo", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si el username no cambia, solo actualizar
                val uri = Uri.withAppendedPath(contentUri, originalUsername)
                Log.d("ClientActualize", "Actualizando usuario: $originalUsername con valores: $contentValues")
                val rowsUpdated = contentResolver.update(uri, contentValues, null, null)

                if (rowsUpdated > 0) {
                    Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            finish()
        }

        val btnBack = findViewById<Button>(R.id.back)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
