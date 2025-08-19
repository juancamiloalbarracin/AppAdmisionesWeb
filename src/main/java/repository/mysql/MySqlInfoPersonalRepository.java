package repository.mysql;

import repository.InfoPersonalRepository;
import utils.DatabaseConnection;

import java.util.Map;

public class MySqlInfoPersonalRepository implements InfoPersonalRepository {
    @Override
    public Map<String, String> getByEmail(String email) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.obtenerInformacionPersonal(email);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public boolean saveFromApi(String email, Map<String, String> payload) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.guardarInformacionPersonal(
                    email,
                    payload.getOrDefault("nombres", ""),
                    payload.getOrDefault("apellidos", ""),
                    payload.getOrDefault("fechaNacimiento", ""),
                    payload.getOrDefault("lugarNacimiento", ""),
                    payload.getOrDefault("tipoDocumento", ""),
                    payload.getOrDefault("numeroDocumento", ""),
                    payload.getOrDefault("genero", ""),
                    payload.getOrDefault("estadoCivil", ""),
                    payload.getOrDefault("direccion", ""),
                    payload.getOrDefault("telefono", ""),
                    payload.getOrDefault("celular", ""),
                    payload.getOrDefault("emailPersonal", "")
            );
        } finally {
            db.closeConnection();
        }
    }
}
