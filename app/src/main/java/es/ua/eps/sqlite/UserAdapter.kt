package es.ua.eps.sqlite

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val context: Context, val cursor: Cursor) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Definir los índices de las columnas
    private val usernameColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.EMAIL)
    private val fullNameColumnIndex = cursor.getColumnIndex(UserContract.UserEntry.NAME)

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userValue: TextView = itemView.findViewById(R.id.uservalue)
        val usernameValue: TextView = itemView.findViewById(R.id.usernamevalue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflar el layout de item
        val view = LayoutInflater.from(context)
            .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // Mover el cursor a la posición correspondiente
        cursor.moveToPosition(position)

        // Obtener los valores del cursor
        val username = cursor.getString(usernameColumnIndex)
        val fullName = cursor.getString(fullNameColumnIndex)

        // Asignar los valores a las vistas
        holder.userValue.text = username
        holder.usernameValue.text = fullName
    }

    override fun getItemCount(): Int {
        return cursor.count // El número de elementos en el cursor
    }

    // Cerrar el cursor cuando ya no se necesite
    override fun onViewRecycled(holder: UserViewHolder) {
        super.onViewRecycled(holder)
        if (!cursor.isClosed) {
            cursor.close()
        }
    }
}
