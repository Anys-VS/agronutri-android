package cl.duoc.agronutri.data

import android.database.sqlite.SQLiteConstraintException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ResultadoRegistro {
    data object Ok : ResultadoRegistro()
    data object CorreoExistente : ResultadoRegistro()
}

class AuthRepository(private val dao: UsuarioDao) {

    suspend fun registrar(
        nombre: String,
        correo: String,
        rol: String,
        clave: String
    ): ResultadoRegistro = withContext(Dispatchers.Default) {
        val correoNorm = correo.trim().lowercase()
        if (dao.buscarPorCorreo(correoNorm) != null) {
            return@withContext ResultadoRegistro.CorreoExistente
        }
        val sal = Contrasenas.nuevaSal()
        val usuario = Usuario(
            nombre = nombre.trim(),
            correo = correoNorm,
            rol = rol,
            hashClave = Contrasenas.aBase64(Contrasenas.hash(clave, sal)),
            sal = Contrasenas.aBase64(sal),
            consentimientoEn = System.currentTimeMillis()
        )
        try {
            dao.insertar(usuario)
            ResultadoRegistro.Ok
        } catch (e: SQLiteConstraintException) {
            ResultadoRegistro.CorreoExistente
        }
    }

    suspend fun login(correo: String, clave: String): Usuario? =
        withContext(Dispatchers.Default) {
            val usuario = dao.buscarPorCorreo(correo.trim().lowercase())
                ?: return@withContext null
            if (Contrasenas.verificar(clave, usuario.sal, usuario.hashClave)) usuario else null
        }
}