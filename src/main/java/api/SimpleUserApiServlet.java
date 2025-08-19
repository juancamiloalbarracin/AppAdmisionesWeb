package api;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import models.Usuario;
import utils.DatabaseConnection;

/**
 * SimpleUserApiServlet - API REST simplificada para registro de usuarios
 * Usa parsing manual de JSON para evitar problemas con Jackson
 */
@WebServlet("/api/users/simple/*")
public class SimpleUserApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if ("/register".equals(pathInfo)) {
            handleRegister(request, response);
        } else {
            sendErrorResponse(response, 404, "Endpoint no encontrado");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            // Leer el cuerpo de la petición
            StringBuilder jsonBody = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            
            String json = jsonBody.toString();
            System.out.println("JSON recibido: " + json);
            
            // Parsing manual simple de JSON
            Map<String, String> data = parseSimpleJson(json);
            
            // Validaciones básicas
            String nombres = data.get("nombres");
            String apellidos = data.get("apellidos");
            String email = data.get("email");
            String password = data.get("password");
            
            if (nombres == null || nombres.trim().isEmpty()) {
                sendErrorResponse(response, 400, "Los nombres son obligatorios");
                return;
            }
            if (apellidos == null || apellidos.trim().isEmpty()) {
                sendErrorResponse(response, 400, "Los apellidos son obligatorios");
                return;
            }
            if (email == null || email.trim().isEmpty()) {
                sendErrorResponse(response, 400, "El email es obligatorio");
                return;
            }
            if (password == null || password.length() < 8) {
                sendErrorResponse(response, 400, "La contraseña debe tener al menos 8 caracteres");
                return;
            }
            
            // Crear usuario
            Usuario usuario = new Usuario(nombres, apellidos, email, email, password);
            
            // Insertar en base de datos
            DatabaseConnection db = new DatabaseConnection();
            String resultado = db.insertUsuario(usuario);
            db.closeConnection();
            
            if ("EXITO".equals(resultado)) {
                // Respuesta exitosa
                response.setStatus(201);
                response.getWriter().write("{\"success\":true,\"message\":\"Usuario registrado correctamente\",\"email\":\"" + email + "\"}");
            } else {
                sendErrorResponse(response, 400, "Error al registrar usuario: " + resultado);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Parser JSON manual muy básico para evitar dependencias
     */
    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> result = new HashMap<>();
        
        // Remover llaves externas
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        // Dividir por comas
        String[] pairs = json.split(",");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                result.put(key, value);
            }
        }
        
        return result;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        response.setStatus(status);
        response.getWriter().write("{\"success\":false,\"error\":\"" + message + "\"}");
    }
}
