package repository;

import java.util.List;
import java.util.Map;

/**
 * SolicitudRepository
 * Abstracción para gestionar solicitudes/radicados.
 */
public interface SolicitudRepository {
    /**
     * Crea una solicitud/radicado.
     */
    boolean create(String userEmail, Map<String, Object> data);

    /**
     * Lista solicitudes por email de usuario.
     */
    List<Map<String, Object>> listByUserEmail(String userEmail, int limit, int offset);

    /**
     * Busca por número de radicado si existiera en el modelo relacional.
     */
    Map<String, Object> findByNumeroRadicado(String numero);
}
