package cl.duoc.agronutri.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroTerrenoDao {
    @Insert
    suspend fun insertar(registro: RegistroTerreno)

    @Query("SELECT * FROM registros_terreno ORDER BY creadoEn DESC")
    fun observar(): Flow<List<RegistroTerreno>>

    @Query("DELETE FROM registros_terreno WHERE id = :id")
    suspend fun borrar(id: Long)
}
