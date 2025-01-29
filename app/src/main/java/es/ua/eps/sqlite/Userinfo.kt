package es.ua.eps.sqlite

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Userinfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)

        val name = intent.getStringExtra("NAME") ?: ""
        val email = intent.getStringExtra("EMAIL") ?: ""

        findViewById<TextView>(R.id.welcome_text).text = "\t\t\t  \t\t\t  WELCOME:   $name"
        findViewById<TextView>(R.id.username_label).text = "\t\t\t  \t\t\t User Name:   $email"

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }
    }
}