package cl.duoc.agronutri.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Usuario::class, RegistroTerreno::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun registroTerrenoDao(): RegistroTerrenoDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agronutri.db"
                )
                    // Versión 2 agrega la tabla de registros. En el MVP solo hay datos ficticios,
                    // así que al cambiar de versión se borra la base en vez de migrarla.
                    .fallbackToDestructiveMigration(true)
                    .build().also { instancia = it }
            }
        }
    }
}