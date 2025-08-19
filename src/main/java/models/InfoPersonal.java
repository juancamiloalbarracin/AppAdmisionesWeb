package models;

/**
 * Clase InfoPersonal que representa la información personal de un usuario.
 */
public class InfoPersonal {
    private int id;
    private String userEmail;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String fechaNacimiento;
    private String lugarNacimiento;
    private String genero;
    private String estadoCivil;
    private String direccion;
    private String telefono;
    private String celular;
    private String emailPersonal;
    private String nombreContactoEmergencia;
    private String telefonoContactoEmergencia;
    private String relacionContactoEmergencia;

    // Constructor vacío
    public InfoPersonal() {}

    // Constructor completo
    public InfoPersonal(String userEmail, String tipoDocumento, String numeroDocumento, 
                       String nombres, String apellidos, String fechaNacimiento, 
                       String lugarNacimiento, String genero, String estadoCivil, 
                       String direccion, String telefono, String celular, 
                       String emailPersonal, String nombreContactoEmergencia, 
                       String telefonoContactoEmergencia, String relacionContactoEmergencia) {
        this.userEmail = userEmail;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.lugarNacimiento = lugarNacimiento;
        this.genero = genero;
        this.estadoCivil = estadoCivil;
        this.direccion = direccion;
        this.telefono = telefono;
        this.celular = celular;
        this.emailPersonal = emailPersonal;
        this.nombreContactoEmergencia = nombreContactoEmergencia;
        this.telefonoContactoEmergencia = telefonoContactoEmergencia;
        this.relacionContactoEmergencia = relacionContactoEmergencia;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getLugarNacimiento() { return lugarNacimiento; }
    public void setLugarNacimiento(String lugarNacimiento) { this.lugarNacimiento = lugarNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }

    public String getEmailPersonal() { return emailPersonal; }
    public void setEmailPersonal(String emailPersonal) { this.emailPersonal = emailPersonal; }

    public String getNombreContactoEmergencia() { return nombreContactoEmergencia; }
    public void setNombreContactoEmergencia(String nombreContactoEmergencia) { this.nombreContactoEmergencia = nombreContactoEmergencia; }

    public String getTelefonoContactoEmergencia() { return telefonoContactoEmergencia; }
    public void setTelefonoContactoEmergencia(String telefonoContactoEmergencia) { this.telefonoContactoEmergencia = telefonoContactoEmergencia; }

    public String getRelacionContactoEmergencia() { return relacionContactoEmergencia; }
    public void setRelacionContactoEmergencia(String relacionContactoEmergencia) { this.relacionContactoEmergencia = relacionContactoEmergencia; }
}
