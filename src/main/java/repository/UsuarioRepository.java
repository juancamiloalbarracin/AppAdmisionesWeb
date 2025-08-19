package repository;

import models.Usuario;

/**
 * UsuarioRepository
 * Capa de abstracción para operaciones sobre usuarios.
 * Fase 1: Implementación MySQL; luego se añadirá Mongo.
 */
public interface UsuarioRepository {
    /**
     * Valida credenciales.
     */
    boolean validateLogin(String email, String password);

    /**
     * Obtiene un usuario por email (o null si no existe).
     */
    Usuario findByEmail(String email);

    /**
     * Crea un usuario. Devuelve códigos: "EXITO", "ERROR_CONEXION",
     * "ERROR_EMAIL_DUPLICADO", "ERROR_USERNAME_DUPLICADO",
     * "ERROR_DATOS_DUPLICADOS", "ERROR_BD".
     */
    String create(Usuario usuario);

    // Reservado para fases siguientes (no obligatorio en Fase 1)
    // boolean updateInfoPersonal(...)
    // boolean updateInfoAcademica(...)
}
