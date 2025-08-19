package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import repository.RepositoryFactory;
import repository.SolicitudRepository;

@WebServlet("/api/solicitudes/*")
public class SolicitudesApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/list")) {
            handleListSolicitudes(request, response);
        } else if (pathInfo == null || "".equals(pathInfo) || "/".equals(pathInfo)) {
            // Default GET behavior - list solicitudes
            handleListSolicitudes(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Endpoint no válido: " + pathInfo + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/radicar")) {
            handleRadicarSolicitud(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Endpoint no válido: " + pathInfo + "\"}");
        }
    }

    private void handleListSolicitudes(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Sesión no válida\"}");
            return;
        }
        
    String userEmail = (String) session.getAttribute("userEmail");
    System.out.println("[Solicitudes/List] userEmail=" + userEmail);
        
        SolicitudRepository repo = RepositoryFactory.getSolicitudRepository();
        int limit = 100;
        int offset = 0;
        List<Map<String, Object>> items = repo.listByUserEmail(userEmail, limit, offset);

        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"data\":[");

        boolean first = true;
        for (Map<String, Object> row : items) {
            if (!first) json.append(",");
            first = false;
            String id = String.valueOf(row.getOrDefault("id", ""));
            String numeroRadicado = String.valueOf(row.getOrDefault("numero_radicado", ""));
            if (numeroRadicado == null || numeroRadicado.isEmpty()) {
                // Fallback compatible con MySQL anterior basado en id
                numeroRadicado = id != null && !id.isEmpty() ? (id.startsWith("RAD-") ? id : ("RAD-" + id)) : generarNumeroRadicado();
            }
            String tipo = safeStr(row.get("tipo_solicitud"));
            String dep = safeStr(row.get("email_notificacion"));
            String asunto = safeStr(row.get("asunto"));
            String descripcion = safeStr(row.get("descripcion"));
            String telefono = safeStr(row.get("telefono_contacto"));
            String fecha = String.valueOf(row.getOrDefault("fecha_radicado", ""));

            json.append("{")
                .append("\"id\":\"").append(escapeJson(id)).append("\",")
                .append("\"numeroRadicado\":\"").append(escapeJson(numeroRadicado)).append("\",")
                .append("\"tipoSolicitud\":\"").append(escapeJson(tipo)).append("\",")
                .append("\"dependenciaDirigida\":\"").append(escapeJson(dep)).append("\",")
                .append("\"asunto\":\"").append(escapeJson(asunto)).append("\",")
                .append("\"descripcion\":\"").append(escapeJson(descripcion)).append("\",")
                .append("\"documentosAdjuntos\":\"\",")
                .append("\"estado\":\"Radicada\",")
                .append("\"fechaRadicacion\":\"").append(escapeJson(fecha)).append("\",")
                .append("\"telefonoContacto\":\"").append(escapeJson(telefono)).append("\"")
                .append("}");
        }

    json.append("],");
    json.append("\"queriedEmail\":\"").append(escapeJson(userEmail)).append("\",");
    json.append(items.isEmpty() ? "\"message\":\"No se encontraron solicitudes radicadas\"}" : "\"message\":\"Solicitudes encontradas\"}");
        out.write(json.toString());
    }

    private void handleRadicarSolicitud(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"error\":\"Sesión no válida\"}");
            return;
        }
        
        String userEmail = (String) session.getAttribute("userEmail");
        
        try {
            // Leer el cuerpo de la petición
            StringBuilder requestBody = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            
            // Parsear JSON manualmente
            String jsonData = requestBody.toString();
            String tipoSolicitud = extractJsonValue(jsonData, "tipo_solicitud");
            String asunto = extractJsonValue(jsonData, "asunto");
            String descripcion = extractJsonValue(jsonData, "descripcion");
            String telefonoContacto = extractJsonValue(jsonData, "telefono_contacto");
            String emailNotificacion = extractJsonValue(jsonData, "email_notificacion");
            
            // Validaciones básicas
            if (tipoSolicitud.isEmpty() || asunto.isEmpty() || descripcion.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"Todos los campos obligatorios deben ser completados\"}");
                return;
            }
            
            // Preparar datos para repositorio (compatible MySQL/Mongo)
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("tipo_solicitud", tipoSolicitud);
            data.put("asunto", asunto);
            data.put("descripcion", descripcion);
            data.put("telefono_contacto", telefonoContacto);
            data.put("email_notificacion", emailNotificacion);

            // Generar un numero de radicado legible; lo guardaremos si el backend lo soporta
            String numeroRadicado = generarNumeroRadicado();
            data.put("numero_radicado", numeroRadicado);

            SolicitudRepository repo = RepositoryFactory.getSolicitudRepository();
            boolean ok = repo.create(userEmail, data);
            if (ok) {
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"success\":true,");
                json.append("\"message\":\"Solicitud radicada exitosamente\",");
                json.append("\"numeroRadicado\":\"").append(numeroRadicado).append("\"");
                json.append("}");
                out.write(json.toString());
            } else {
                out.write("{\"success\":false,\"message\":\"No se pudo radicar la solicitud\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Error al procesar la petición\"}");
        }
    }

    private String generarNumeroRadicado() {
        // Formato: RAD-YYYYMMDD-XXXX donde XXXX es un número aleatorio
        java.time.LocalDate fecha = java.time.LocalDate.now();
        String fechaStr = fecha.toString().replace("-", "");
        
        Random random = new Random();
        int numeroAleatorio = 1000 + random.nextInt(9000); // Número de 4 dígitos
        
        return "RAD-" + fechaStr + "-" + numeroAleatorio;
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return "";
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return "";
        
        return json.substring(startIndex, endIndex);
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }

    private String safeStr(Object o) {
        return o == null ? "" : String.valueOf(o);
    }
}
