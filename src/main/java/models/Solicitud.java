package models;

import java.util.Date;

/**
 * Clase Solicitud que representa una solicitud radicada por un usuario.
 */
public class Solicitud {
    private int id;
    private String userEmail;
    private String tipoSolicitud;
    private String dependenciaDirigida;
    private String asunto;
    private String descripcion;
    private String documentosAdjuntos;
    private String estado;
    private Date fechaRadicacion;
    private String numeroRadicado;

    // Constructor vacío
    public Solicitud() {}

    // Constructor completo
    public Solicitud(String userEmail, String tipoSolicitud, String dependenciaDirigida,
                    String asunto, String descripcion, String documentosAdjuntos, String estado) {
        this.userEmail = userEmail;
        this.tipoSolicitud = tipoSolicitud;
        this.dependenciaDirigida = dependenciaDirigida;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.documentosAdjuntos = documentosAdjuntos;
        this.estado = estado;
        this.fechaRadicacion = new Date();
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTipoSolicitud() { return tipoSolicitud; }
    public void setTipoSolicitud(String tipoSolicitud) { this.tipoSolicitud = tipoSolicitud; }

    public String getDependenciaDirigida() { return dependenciaDirigida; }
    public void setDependenciaDirigida(String dependenciaDirigida) { this.dependenciaDirigida = dependenciaDirigida; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDocumentosAdjuntos() { return documentosAdjuntos; }
    public void setDocumentosAdjuntos(String documentosAdjuntos) { this.documentosAdjuntos = documentosAdjuntos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(Date fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public String getNumeroRadicado() { return numeroRadicado; }
    public void setNumeroRadicado(String numeroRadicado) { this.numeroRadicado = numeroRadicado; }
}
