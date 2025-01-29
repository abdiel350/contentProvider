package es.ua.eps.sqlite

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private var backupFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //boton para cerrar la actividad
        findViewById<Button>(R.id.close).setOnClickListener {
            finish()
        }

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)

        findViewById<Button>(R.id.login).setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userUri = Uri.withAppendedPath(UserContract.CONTENT_URI, username)
            val cursor = contentResolver.query(
                userUri,
                arrayOf(
                    UserContract.UserEntry.NAME,
                    UserContract.UserEntry.EMAIL,
                    UserContract.UserEntry.PASSWORD
                ),
                null, null, null
            )

            if (cursor?.moveToFirst() == true) {
                val dbPassword = cursor.getString(
                    cursor.getColumnIndexOrThrow(UserContract.UserEntry.PASSWORD)
                )

                if (password == dbPassword) {
                    val name = cursor.getString(
                        cursor.getColumnIndexOrThrow(UserContract.UserEntry.NAME)
                    )
                    val email = cursor.getString(
                        cursor.getColumnIndexOrThrow(UserContract.UserEntry.EMAIL)
                    )

                    val intent = Intent(this,Userinfo::class.java).apply {
                        putExtra("NAME", name)
                        putExtra("EMAIL", email)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        // Forzar colores para los elementos del menú
        for (i in 0 until (menu?.size() ?: 0)) {
            val menuItem = menu?.getItem(i)
            val spannableTitle = SpannableString(menuItem?.title)
            spannableTitle.setSpan(ForegroundColorSpan(Color.BLACK), 0, spannableTitle.length, 0)
            menuItem?.title = spannableTitle
        }
        return true
    }

    // Manejar ítems del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.create_backup -> {
                Toast.makeText(this, "Función deshabilitada", Toast.LENGTH_SHORT).show()
              //  backupFileName = "UsuariosBackup"
               // createBackup()
                true
            }
            R.id.restore_backup -> {
                Toast.makeText(this, "Función deshabilitada", Toast.LENGTH_SHORT).show()
               // restoreBackup()
                true
            }
            R.id.manage_users -> {
                manageUsers()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createBackup() {
        backupFileName?.let { fileName ->
            try {
                val resolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Files.FileColumns.DISPLAY_NAME, "$fileName.db")
                    put(MediaStore.Files.FileColumns.MIME_TYPE, "application/x-sqlite3")
                    put(MediaStore.Files.FileColumns.RELATIVE_PATH, "Download/DatabaseBackups")
                }

                // Insertar en MediaStore para crear el archivo
                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                if (uri != null) {
                    val outputStream = resolver.openOutputStream(uri)
                    val databasePath = getDatabasePath("usuarios.db")

                    if (databasePath.exists()) {
                        val inputStream = databasePath.inputStream()
                        outputStream?.use { output ->
                            inputStream.copyTo(output)
                        }
                        Toast.makeText(
                            this,
                            "Backup creado exitosamente en Descargas/DatabaseBackups.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(this, "La base de datos no existe.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Error al crear el archivo de backup.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al crear el backup: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun restoreBackup() {
        // Abrir el selector de archivos para elegir el archivo de respaldo desde ubicaciones públicas
        filePickerLauncher.launch("*/*")
    }

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                // Ruta de la base de datos actual (área privada)
                val databasePath = File("/data/data/es.ua.eps.sqlite/databases/usuarios.db")

                // Asegurando que la base de datos exista
                if (!databasePath.exists()) {
                    databasePath.parentFile?.mkdirs()
                    databasePath.createNewFile()
                }

                // Obtener el InputStream del archivo de respaldo seleccionado
                val inputStream = contentResolver.openInputStream(uri)

                // Copiar los datos desde el archivo de respaldo al archivo de la base de datos
                if (inputStream != null) {
                    databasePath.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    Toast.makeText(
                        this,
                        "Backup restaurado exitosamente.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "No se pudo abrir el archivo de respaldo.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al restaurar el backup: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se seleccionó ningún archivo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun manageUsers() {
        val intent = Intent(this, UserManagement::class.java)
        startActivity(intent)
    }
}