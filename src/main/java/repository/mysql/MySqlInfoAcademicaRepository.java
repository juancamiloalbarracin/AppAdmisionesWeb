package repository.mysql;

import repository.InfoAcademicaRepository;
import utils.DatabaseConnection;

import java.util.Map;

public class MySqlInfoAcademicaRepository implements InfoAcademicaRepository {
    @Override
    public Map<String, String> getByEmail(String email) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.obtenerInformacionAcademica(email);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public boolean saveFromApi(String email, Map<String, String> payload) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            return db.guardarInformacionAcademica(
                    email,
                    payload.getOrDefault("nivel", ""),
                    payload.getOrDefault("sede", ""),
                    payload.getOrDefault("gradoAcademico", ""),
                    payload.getOrDefault("periodoAdmision", ""),
                    payload.getOrDefault("metodologia", ""),
                    payload.getOrDefault("jornada", ""),
                    payload.getOrDefault("planDecision", ""),
                    payload.getOrDefault("gradoSeleccionado", ""),
                    payload.getOrDefault("pais", ""),
                    payload.getOrDefault("gradoObtenido", ""),
                    payload.getOrDefault("fechaGraduacion", "")
            );
        } finally {
            db.closeConnection();
        }
    }
}
