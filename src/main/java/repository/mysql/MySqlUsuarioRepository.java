package repository.mysql;

import models.Usuario;
import repository.UsuarioRepository;
import utils.DatabaseConnection;

/**
 * Implementación MySQL del repositorio de usuarios, apoyada en DatabaseConnection
 * para mantener el comportamiento actual en Fase 1.
 */
public class MySqlUsuarioRepository implements UsuarioRepository {

    @Override
    public boolean validateLogin(String email, String password) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.validarUsuario(email, password);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public Usuario findByEmail(String email) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.obtenerUsuarioPorUsername(email);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public String create(Usuario usuario) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.insertUsuario(usuario);
        } finally {
            db.closeConnection();
        }
    }
}
