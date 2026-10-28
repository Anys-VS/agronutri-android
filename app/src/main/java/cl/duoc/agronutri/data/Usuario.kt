package cl.duoc.agronutri.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val rol: String,          // "AGRICULTOR" o "ASESOR"
    val hashClave: String,    // Base64
    val sal: String,          // Base64
    val consentimientoEn: Long
)