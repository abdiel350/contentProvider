package es.ua.eps.sqlite

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class UserManagement : AppCompatActivity() {

    private lateinit var userSpinner: Spinner
    private var selectedUser: User? = null

    private val updateUserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                resetSpinnerToDefault()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        userSpinner = findViewById(R.id.userspinner)
        loadUsersIntoSpinner()

        val btnNewUser = findViewById<Button>(R.id.newUser)
        btnNewUser.setOnClickListener {
            val intent = Intent(this, NewUser::class.java)
            startActivity(intent)
        }

        val btnUpdateUser = findViewById<Button>(R.id.updateUser)
        btnUpdateUser.setOnClickListener {
            selectedUser?.let { user ->
                val intent = Intent(this, ClientActualize::class.java)
                intent.putExtra("username", user.username)
                intent.putExtra("fullName", user.fullName)
                intent.putExtra("password", user.password)
                updateUserLauncher.launch(intent)
            } ?: run {
                showToast("Por favor, selecciona un usuario primero")
            }
        }

        val btnDeleteUser = findViewById<Button>(R.id.deleteUser)
        btnDeleteUser.setOnClickListener {
            selectedUser?.let { user ->
                showDeleteConfirmationDialog(user)
            } ?: run {
                showToast("Por favor, selecciona un usuario primero")
            }
        }

        val btnListUsers = findViewById<Button>(R.id.listUsers)
        btnListUsers.setOnClickListener {
            val intent = Intent(this, ListUser::class.java)
            startActivity(intent)
        }

        val btnBack = findViewById<Button>(R.id.back)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        resetSpinnerToDefault()
    }

    private fun loadUsersIntoSpinner() {
        val users = getAllUsers()
        val userNames = mutableListOf("Selecciona")
        userNames.addAll(users.map { it.username })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userSpinner.adapter = adapter

        userSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedUser = if (position == 0) null else users[position - 1]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedUser = null
            }
        }
        userSpinner.setSelection(0)
    }

    private fun resetSpinnerToDefault() {
        loadUsersIntoSpinner()
        userSpinner.post {
            userSpinner.setSelection(0, false)
        }
        selectedUser = null
    }

    @SuppressLint("Range")
    private fun getAllUsers(): List<User> {
        val cursor = contentResolver.query(UserContract.CONTENT_URI, null, null, null, null)
        val users = mutableListOf<User>()
        cursor?.let {
            while (it.moveToNext()) {
                val username = it.getString(it.getColumnIndex(UserContract.UserEntry.EMAIL))
                val fullName = it.getString(it.getColumnIndex(UserContract.UserEntry.NAME))
                val password = it.getString(it.getColumnIndex(UserContract.UserEntry.PASSWORD))
                users.add(User(username, fullName, password))
            }
            it.close()
        }
        return users
    }

    private fun deleteUser(user: User) {
        val uri = Uri.withAppendedPath(UserContract.CONTENT_URI, user.username)
        val rowsDeleted = contentResolver.delete(uri, null, null)
        if (rowsDeleted > 0) {
            showToast("Usuario eliminado correctamente")
            resetSpinnerToDefault()
        } else {
            showToast("Error al eliminar el usuario")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmationDialog(user: User) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Eliminar Usuario?")
            .setMessage("¿Realmente quieres eliminar al usuario seleccionado?")
            .setPositiveButton("OK") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("CANCEL") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()
    }
}
