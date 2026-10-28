package cl.duoc.agronutri.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Una fila por cada registro hecho en terreno: datos del lote, resultado del cálculo y foto.
@Entity(tableName = "registros_terreno")
data class RegistroTerreno(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val cultivo: String,
    val lote: String,
    val metaRendimiento: Double,   // t/há
    val eficienciaRiego: Double,   // %
    val n: Double,                 // kg/há
    val p2o5: Double,
    val k2o: Double,
    val cao: Double,
    val mgo: Double,
    val fotoUri: String,           // "" si no hay foto
    val creadoEn: Long,
    val sincronizado: Boolean = false  // se enviará al servidor en una etapa futura
)
