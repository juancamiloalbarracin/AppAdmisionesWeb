package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * DatabaseConnection - MIGRADO a aplicación web
 * PRESERVA: Toda la funcionalidad original sin cambios
 * AÑADE: Compatibilidad para entorno web y mejoras de seguridad
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/app_administrativa";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    
    private Connection connection;
    // Email del usuario logueado, usada para relacionar datos
    private static String currentUserEmail;

    /**
     * Constructor - PRESERVADO: Exactamente la misma lógica de conexión + MEJORADO con logs detallados
     */
    public DatabaseConnection() {
        try {
            // Cargar el driver explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL cargado correctamente");
            
            // Intentar conectar directamente a la base de datos
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos: " + URL);
            // Asegurar que todas las tablas existen
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR CRÍTICO: Driver MySQL no encontrado");
            System.err.println("Verifique que mysql-connector-j está en el classpath");
            e.printStackTrace();
            connection = null;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos. Intentando crear la base de datos...");
            System.err.println("URL: " + URL);
            System.err.println("Error específico: " + e.getMessage());
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            
            try {
                // Conectar sin especificar base de datos para crearla
                String baseURL = "jdbc:mysql://localhost:3306/";
                Connection tempConnection = DriverManager.getConnection(baseURL, USER, PASSWORD);
                
                // Crear la base de datos
                java.sql.Statement stmt = tempConnection.createStatement();
                stmt.execute("CREATE DATABASE IF NOT EXISTS app_administrativa");
                stmt.execute("USE app_administrativa");
                
                // Crear la tabla usuarios
                String createTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nombre VARCHAR(100) NOT NULL, " +
                    "apellido VARCHAR(100) NOT NULL, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "UNIQUE (email)" +
                    ")";
                stmt.execute(createTable);
                // Asegurar índice en usuarios.email aunque la tabla ya existiera sin índice
                try {
                    stmt.execute("ALTER TABLE usuarios ADD UNIQUE (email)");
                } catch (SQLException ignore) {
                    // índice ya existente, ignorar
                }
                // Crear tabla de información personal
                String createPersonal = "CREATE TABLE IF NOT EXISTS informacion_personal (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "primer_nombre VARCHAR(100), " +
                    "segundo_nombre VARCHAR(100), " +
                    "apellidos VARCHAR(200), " +
                    "sexo_biologico VARCHAR(50), " +
                    "grupo_sanguineo VARCHAR(50), " +
                    "tipo_documento VARCHAR(100), " +
                    "numero_documento VARCHAR(100), " +
                    "fecha_expedicion_documento VARCHAR(20), " +
                    "pais_nacimiento VARCHAR(100), " +
                    "ciudad_nacimiento VARCHAR(100), " +
                    "fecha_nacimiento VARCHAR(20), " +
                    "telefono_celular VARCHAR(50), " +
                    "correo_electronico VARCHAR(100), " +
                    "tipo_direccion VARCHAR(100), " +
                    "direccion TEXT, " +
                    "casa_apto_barrio VARCHAR(200), " +
                    "tiene_sisben VARCHAR(10), " +
                    "tiene_eps VARCHAR(10), " +
                    "tiene_discapacidad VARCHAR(10), " +
                    "primer_nombre_acudiente VARCHAR(100), " +
                    "segundo_nombre_acudiente VARCHAR(100), " +
                    "apellidos_acudiente VARCHAR(200), " +
                    "telefono_acudiente VARCHAR(50), " +
                    "correo_acudiente VARCHAR(100), " +
                    "UNIQUE (email), " +
                    "FOREIGN KEY (email) REFERENCES usuarios(email)" +
                    ")";
                stmt.execute(createPersonal);
                // Asegurar tamaño de grupo_sanguineo aunque la tabla existiera previa con tamaño menor
                try {
                    stmt.execute("ALTER TABLE informacion_personal MODIFY grupo_sanguineo VARCHAR(50)");
                } catch (SQLException ignore) {
                }
                // Crear tabla de información académica
                String createAcademica = "CREATE TABLE IF NOT EXISTS informacion_academica (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "nivel VARCHAR(50), " +
                    "sede VARCHAR(100), " +
                    "grado_academico VARCHAR(50), " +
                    "periodo_admision VARCHAR(100), " +
                    "metodologia VARCHAR(100), " +
                    "jornada VARCHAR(50), " +
                    "plan_decision VARCHAR(100), " +
                    "grado_seleccionado VARCHAR(100), " +
                    "pais VARCHAR(100), " +
                    "grado_obtenido VARCHAR(100), " +
                    "fecha_graduacion VARCHAR(20), " +
                    "UNIQUE (email), " +
                    "FOREIGN KEY (email) REFERENCES usuarios(email)" +
                    ")";
                stmt.execute(createAcademica);
                // Crear tabla de radicados
                String createRadicados = "CREATE TABLE IF NOT EXISTS radicados (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "tipo_solicitud VARCHAR(100), " +
                    "asunto VARCHAR(200), " +
                    "descripcion TEXT, " +
                    "telefono_contacto VARCHAR(50), " +
                    "email_notificacion VARCHAR(100), " +
                    "fecha_radicado DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (email) REFERENCES usuarios(email)" +
                    ")";
                stmt.execute(createRadicados);
                
                tempConnection.close();
                
                // Ahora conectar a la base de datos creada
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Base de datos creada y conexión establecida.");
                // Asegurar que las tablas secundarias existen
                createTables();
                
            } catch (SQLException ex) {
                System.err.println("Error al crear la base de datos:");
                System.err.println("URL: " + URL);
                System.err.println("Usuario: " + USER);
                System.err.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * PRESERVADO: Métodos originales sin cambios
     */
    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void executeQuery(String query) {
        // Implementation for executing a query can be added here
    }
    
    /**
     * PRESERVADO: Insertar usuario - MEJORADO con manejo específico de excepciones
     */
    public String insertUsuario(models.Usuario usuario) {
        if (connection == null) {
            System.err.println("No hay conexión a la base de datos");
            return "ERROR_CONEXION";
        }
        
        String sql = "INSERT INTO usuarios (nombre, apellido, username, password, email) VALUES (?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getUsername());
            stmt.setString(4, usuario.getPassword());
            stmt.setString(5, usuario.getEmail());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return "EXITO";
            } else {
                return "ERROR_INSERCION";
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            e.printStackTrace();
            
            // Verificar el tipo específico de error
            String errorMsg = e.getMessage().toLowerCase();
            
            if (errorMsg.contains("duplicate entry") || errorMsg.contains("unique")) {
                if (errorMsg.contains("email")) {
                    return "ERROR_EMAIL_DUPLICADO";
                } else if (errorMsg.contains("username")) {
                    return "ERROR_USERNAME_DUPLICADO";
                } else {
                    return "ERROR_DATOS_DUPLICADOS";
                }
            } else if (errorMsg.contains("connection") || errorMsg.contains("communications link failure")) {
                return "ERROR_CONEXION";
            } else {
                return "ERROR_BD";
            }
        }
    }
    
    /**
     * PRESERVADO: Upsert información personal - Sin cambios
     */
    public boolean upsertInformacionPersonal(
            String email, String primerNombre, String segundoNombre,
            String apellidos, String sexoBiologico, String grupoSanguineo,
            String tipoDocumento, String numeroDocumento, String fechaExpedicionDocumento,
            String paisNacimiento, String ciudadNacimiento, String fechaNacimiento,
            String telefonoCelular, String correoElectronico, String tipoDireccion,
            String direccion, String casaAptoBarrio, String tieneSisben,
            String tieneEps, String tieneDiscapacidad,
            String primerNombreAcudiente, String segundoNombreAcudiente,
            String apellidosAcudiente, String telefonoAcudiente, String correoAcudiente) {
        // Asegurar existencia del usuario antes de insertar datos personales
        ensureUserExists(email);
        String sql = "REPLACE INTO informacion_personal (" +
            "email, primer_nombre, segundo_nombre, apellidos, sexo_biologico, grupo_sanguineo, " +
            "tipo_documento, numero_documento, fecha_expedicion_documento, pais_nacimiento, ciudad_nacimiento, fecha_nacimiento, " +
            "telefono_celular, correo_electronico, tipo_direccion, direccion, casa_apto_barrio, " +
            "tiene_sisben, tiene_eps, tiene_discapacidad, primer_nombre_acudiente, segundo_nombre_acudiente, apellidos_acudiente, telefono_acudiente, correo_acudiente) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            stmt.setString(i++, email);
            stmt.setString(i++, primerNombre);
            stmt.setString(i++, segundoNombre);
            stmt.setString(i++, apellidos);
            stmt.setString(i++, sexoBiologico);
            stmt.setString(i++, grupoSanguineo);
            stmt.setString(i++, tipoDocumento);
            stmt.setString(i++, numeroDocumento);
            stmt.setString(i++, fechaExpedicionDocumento);
            stmt.setString(i++, paisNacimiento);
            stmt.setString(i++, ciudadNacimiento);
            stmt.setString(i++, fechaNacimiento);
            stmt.setString(i++, telefonoCelular);
            stmt.setString(i++, correoElectronico);
            stmt.setString(i++, tipoDireccion);
            stmt.setString(i++, direccion);
            stmt.setString(i++, casaAptoBarrio);
            stmt.setString(i++, tieneSisben);
            stmt.setString(i++, tieneEps);
            stmt.setString(i++, tieneDiscapacidad);
            stmt.setString(i++, primerNombreAcudiente);
            stmt.setString(i++, segundoNombreAcudiente);
            stmt.setString(i++, apellidosAcudiente);
            stmt.setString(i++, telefonoAcudiente);
            stmt.setString(i++, correoAcudiente);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * PRESERVADO: Upsert información académica - Sin cambios
     */
    public boolean upsertInformacionAcademica(
            String email, String nivel, String sede, String gradoAcademico,
            String periodoAdmision, String metodologia, String jornada,
            String planDecision, String gradoSeleccionado, String pais,
            String gradoObtenido, String fechaGraduacion) {
        // Asegurar existencia del usuario antes de insertar datos académicos
        ensureUserExists(email);
        String sql = "REPLACE INTO informacion_academica (" +
            "email, nivel, sede, grado_academico, periodo_admision, metodologia, jornada, plan_decision, grado_seleccionado, pais, grado_obtenido, fecha_graduacion) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            int i = 1;
            stmt.setString(i++, email);
            stmt.setString(i++, nivel);
            stmt.setString(i++, sede);
            stmt.setString(i++, gradoAcademico);
            stmt.setString(i++, periodoAdmision);
            stmt.setString(i++, metodologia);
            stmt.setString(i++, jornada);
            stmt.setString(i++, planDecision);
            stmt.setString(i++, gradoSeleccionado);
            stmt.setString(i++, pais);
            stmt.setString(i++, gradoObtenido);
            stmt.setString(i++, fechaGraduacion);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * PRESERVADO: Insertar radicado - Sin cambios
     */
    public boolean insertRadicado(
            String email, String tipoSolicitud, String asunto,
            String descripcion, String telefonoContacto, String emailNotificacion) {
        // Asegurar existencia del usuario antes de insertar radicado
        ensureUserExists(email);
        String sql = "INSERT INTO radicados (email, tipo_solicitud, asunto, descripcion, telefono_contacto, email_notificacion) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, tipoSolicitud);
            stmt.setString(3, asunto);
            stmt.setString(4, descripcion);
            stmt.setString(5, telefonoContacto);
            stmt.setString(6, emailNotificacion);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * PRESERVADO: Validar usuario - CORREGIDO para usar email
     */
    public boolean validarUsuario(String email, String password) {
        if (connection == null) {
            System.err.println("No hay conexión a la base de datos");
            return false;
        }
        
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            java.sql.ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al validar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * NUEVO: Método específico para web - Obtener datos completos de usuario por email
     */
    public models.Usuario obtenerUsuarioPorUsername(String email) {
        if (connection == null) {
            System.err.println("No hay conexión a la base de datos");
            return null;
        }
        
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                models.Usuario usuario = new models.Usuario(
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * PRESERVADO: Gestión de usuario actual - Sin cambios
     */
    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }
    
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }

    /**
     * PRESERVADO: Crear tablas - Sin cambios
     */
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Asegurar que estamos en la base de datos correcta
            stmt.execute("USE app_administrativa");
            // Crear la tabla usuarios
            String createTable = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "apellido VARCHAR(100) NOT NULL, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "UNIQUE (email)" +
                ")";
            stmt.execute(createTable);
            // Asegurar índice único en usuarios.email si no existiera
            try {
                stmt.execute("ALTER TABLE usuarios ADD UNIQUE (email)");
            } catch (SQLException ignore) {
                // Puede fallar si el índice ya existe, se ignora
            }

            // Crear tabla de información personal
            String createPersonal = "CREATE TABLE IF NOT EXISTS informacion_personal (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(100) NOT NULL, " +
                "primer_nombre VARCHAR(100), " +
                "segundo_nombre VARCHAR(100), " +
                "apellidos VARCHAR(200), " +
                "sexo_biologico VARCHAR(50), " +
                "grupo_sanguineo VARCHAR(50), " +
                "tipo_documento VARCHAR(100), " +
                "numero_documento VARCHAR(100), " +
                "fecha_expedicion_documento VARCHAR(20), " +
                "pais_nacimiento VARCHAR(100), " +
                "ciudad_nacimiento VARCHAR(100), " +
                "fecha_nacimiento VARCHAR(20), " +
                "telefono_celular VARCHAR(50), " +
                "correo_electronico VARCHAR(100), " +
                "tipo_direccion VARCHAR(100), " +
                "direccion TEXT, " +
                "casa_apto_barrio VARCHAR(200), " +
                "tiene_sisben VARCHAR(10), " +
                "tiene_eps VARCHAR(10), " +
                "tiene_discapacidad VARCHAR(10), " +
                "primer_nombre_acudiente VARCHAR(100), " +
                "segundo_nombre_acudiente VARCHAR(100), " +
                "apellidos_acudiente VARCHAR(200), " +
                "telefono_acudiente VARCHAR(50), " +
                "correo_acudiente VARCHAR(100), " +
                "UNIQUE (email), " +
                "FOREIGN KEY (email) REFERENCES usuarios(email)" +
            ")";
            stmt.execute(createPersonal);
            // Asegurar tamaño de grupo_sanguineo en tabla existente
            try {
                stmt.execute("ALTER TABLE informacion_personal MODIFY grupo_sanguineo VARCHAR(50)");
            } catch (SQLException ignore) {
            }

            // Crear tabla de información académica
            String createAcademica = "CREATE TABLE IF NOT EXISTS informacion_academica (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(100) NOT NULL, " +
                "nivel VARCHAR(50), " +
                "sede VARCHAR(100), " +
                "grado_academico VARCHAR(50), " +
                "periodo_admision VARCHAR(100), " +
                "metodologia VARCHAR(100), " +
                "jornada VARCHAR(50), " +
                "plan_decision VARCHAR(100), " +
                "grado_seleccionado VARCHAR(100), " +
                "pais VARCHAR(100), " +
                "grado_obtenido VARCHAR(100), " +
                "fecha_graduacion VARCHAR(20), " +
                "UNIQUE (email), " +
                "FOREIGN KEY (email) REFERENCES usuarios(email)" +
            ")";
            stmt.execute(createAcademica);

            // Crear tabla de radicados
            String createRadicados = "CREATE TABLE IF NOT EXISTS radicados (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(100) NOT NULL, " +
                "tipo_solicitud VARCHAR(100), " +
                "asunto VARCHAR(200), " +
                "descripcion TEXT, " +
                "telefono_contacto VARCHAR(50), " +
                "email_notificacion VARCHAR(100), " +
                "fecha_radicado DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (email) REFERENCES usuarios(email)" +
            ")";
            stmt.execute(createRadicados);
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * PRESERVADO: Asegurar usuario existe - Sin cambios
     */
    private void ensureUserExists(String email) {
        if (email == null || email.isEmpty()) return;
        try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM usuarios WHERE email = ?")) {
            check.setString(1, email);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                try (PreparedStatement ins = connection.prepareStatement(
                        "INSERT INTO usuarios(nombre, apellido, username, password, email) VALUES (?, ?, ?, ?, ?)")) {
                    ins.setString(1, "Desconocido");
                    ins.setString(2, "Desconocido");
                    ins.setString(3, email);
                    ins.setString(4, "");
                    ins.setString(5, email);
                    ins.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * NUEVO: Métodos para APIs REST propias
     */
    
    /**
     * Obtener información personal del usuario para API
     */
    public java.util.Map<String, String> obtenerInformacionPersonal(String email) {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        
        try {
            ensureUserExists(email);
            String sql = "SELECT * FROM informacion_personal WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                info.put("primerNombre", rs.getString("primer_nombre"));
                info.put("segundoNombre", rs.getString("segundo_nombre"));
                info.put("apellidos", rs.getString("apellidos"));
                info.put("sexoBiologico", rs.getString("sexo_biologico"));
                info.put("grupoSanguineo", rs.getString("grupo_sanguineo"));
                info.put("tipoDocumento", rs.getString("tipo_documento"));
                info.put("numeroDocumento", rs.getString("numero_documento"));
                info.put("fechaExpedicionDocumento", rs.getString("fecha_expedicion_documento"));
                info.put("paisNacimiento", rs.getString("pais_nacimiento"));
                info.put("ciudadNacimiento", rs.getString("ciudad_nacimiento"));
                info.put("fechaNacimiento", rs.getString("fecha_nacimiento"));
                info.put("telefonoCelular", rs.getString("telefono_celular"));
                info.put("correoElectronico", rs.getString("correo_electronico"));
                info.put("tipoDireccion", rs.getString("tipo_direccion"));
                info.put("direccion", rs.getString("direccion"));
                info.put("casaAptoBarrio", rs.getString("casa_apto_barrio"));
                info.put("tieneSisben", rs.getString("tiene_sisben"));
                info.put("tieneEps", rs.getString("tiene_eps"));
                info.put("tieneDiscapacidad", rs.getString("tiene_discapacidad"));
                info.put("primerNombreAcudiente", rs.getString("primer_nombre_acudiente"));
                info.put("segundoNombreAcudiente", rs.getString("segundo_nombre_acudiente"));
                info.put("apellidosAcudiente", rs.getString("apellidos_acudiente"));
                info.put("telefonoAcudiente", rs.getString("telefono_acudiente"));
                info.put("correoAcudiente", rs.getString("correo_acudiente"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener información personal: " + e.getMessage());
            e.printStackTrace();
        }
        
        return info;
    }
    
    /**
     * Guardar información personal del usuario para API
     */
    public boolean guardarInformacionPersonal(String email, String nombres, String apellidos, 
            String fechaNacimiento, String lugarNacimiento, String tipoDocumento, 
            String numeroDocumento, String genero, String estadoCivil, String direccion, 
            String telefono, String celular, String emailPersonal) {
        
        try {
            ensureUserExists(email);
            
            // Verificar si ya existe información
            String checkSql = "SELECT 1 FROM informacion_personal WHERE email = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();
            
            String sql;
            if (rs.next()) {
                // Actualizar - usando las columnas reales de la DB
                sql = "UPDATE informacion_personal SET primer_nombre=?, segundo_nombre=?, apellidos=?, " +
                      "sexo_biologico=?, grupo_sanguineo=?, tipo_documento=?, numero_documento=?, " +
                      "fecha_expedicion_documento=?, pais_nacimiento=?, ciudad_nacimiento=?, " +
                      "fecha_nacimiento=?, telefono_celular=?, correo_electronico=?, tipo_direccion=?, " +
                      "direccion=?, casa_apto_barrio=?, tiene_sisben=?, tiene_eps=?, tiene_discapacidad=?, " +
                      "primer_nombre_acudiente=?, segundo_nombre_acudiente=?, apellidos_acudiente=?, " +
                      "telefono_acudiente=?, correo_acudiente=? WHERE email=?";
            } else {
                // Insertar - usando las columnas reales de la DB
                sql = "INSERT INTO informacion_personal (primer_nombre, segundo_nombre, apellidos, " +
                      "sexo_biologico, grupo_sanguineo, tipo_documento, numero_documento, " +
                      "fecha_expedicion_documento, pais_nacimiento, ciudad_nacimiento, fecha_nacimiento, " +
                      "telefono_celular, correo_electronico, tipo_direccion, direccion, casa_apto_barrio, " +
                      "tiene_sisben, tiene_eps, tiene_discapacidad, primer_nombre_acudiente, " +
                      "segundo_nombre_acudiente, apellidos_acudiente, telefono_acudiente, correo_acudiente, email) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
            
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nombres);
            ps.setString(2, apellidos);
            ps.setString(3, fechaNacimiento);
            ps.setString(4, lugarNacimiento);
            ps.setString(5, tipoDocumento);
            ps.setString(6, numeroDocumento);
            ps.setString(7, genero);
            ps.setString(8, estadoCivil);
            ps.setString(9, direccion);
            ps.setString(10, telefono);
            ps.setString(11, celular);
            ps.setString(12, emailPersonal);
            ps.setString(13, email);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar información personal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener información académica del usuario para API
     */
    public java.util.Map<String, String> obtenerInformacionAcademica(String email) {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        
        try {
            ensureUserExists(email);
            String sql = "SELECT * FROM informacion_academica WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                info.put("nivel", rs.getString("nivel"));
                info.put("sede", rs.getString("sede"));
                info.put("gradoAcademico", rs.getString("grado_academico"));
                info.put("periodoAdmision", rs.getString("periodo_admision"));
                info.put("metodologia", rs.getString("metodologia"));
                info.put("jornada", rs.getString("jornada"));
                info.put("planDecision", rs.getString("plan_decision"));
                info.put("gradoSeleccionado", rs.getString("grado_seleccionado"));
                info.put("pais", rs.getString("pais"));
                info.put("gradoObtenido", rs.getString("grado_obtenido"));
                info.put("fechaGraduacion", rs.getString("fecha_graduacion"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener información académica: " + e.getMessage());
            e.printStackTrace();
        }
        
        return info;
    }
    
    /**
     * Guardar información académica del usuario para API - ACTUALIZADO para columnas reales
     */
    public boolean guardarInformacionAcademica(String email, String nivel, String sede, 
            String gradoAcademico, String periodoAdmision, String metodologia, String jornada, 
            String planDecision, String gradoSeleccionado, String pais, String gradoObtenido, 
            String fechaGraduacion) {
        
        try {
            ensureUserExists(email);
            
            // Verificar si ya existe información
            String checkSql = "SELECT 1 FROM informacion_academica WHERE email = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();
            
            String sql;
            if (rs.next()) {
                // Actualizar - usando columnas reales
                sql = "UPDATE informacion_academica SET nivel=?, sede=?, grado_academico=?, " +
                      "periodo_admision=?, metodologia=?, jornada=?, plan_decision=?, " +
                      "grado_seleccionado=?, pais=?, grado_obtenido=?, fecha_graduacion=? WHERE email=?";
            } else {
                // Insertar - usando columnas reales
                sql = "INSERT INTO informacion_academica (nivel, sede, grado_academico, " +
                      "periodo_admision, metodologia, jornada, plan_decision, grado_seleccionado, " +
                      "pais, grado_obtenido, fecha_graduacion, email) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
            
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, nivel);
            ps.setString(2, sede);
            ps.setString(3, gradoAcademico);
            ps.setString(4, periodoAdmision);
            ps.setString(5, metodologia);
            ps.setString(6, jornada);
            ps.setString(7, planDecision);
            ps.setString(8, gradoSeleccionado);
            ps.setString(9, pais);
            ps.setString(10, gradoObtenido);
            ps.setString(11, fechaGraduacion);
            ps.setString(12, email);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar información académica: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
