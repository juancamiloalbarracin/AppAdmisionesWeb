package models;

/**
 * Clase InfoAcademica que representa la información académica de un usuario.
 */
public class InfoAcademica {
    private int id;
    private String userEmail;
    private String tipoFormacion;
    private String institucionEducativa;
    private String programaAcademico;
    private String fechaGraduacion;
    private String numeroTarjetaProfesional;
    private String universidadTitulo;
    private String tituloObtenido;
    private String fechaTitulo;
    private String idiomas;
    private String certificacionesAdicionales;
    private String experienciaLaboral;

    // Constructor vacío
    public InfoAcademica() {}

    // Constructor completo
    public InfoAcademica(String userEmail, String tipoFormacion, String institucionEducativa,
                        String programaAcademico, String fechaGraduacion, String numeroTarjetaProfesional,
                        String universidadTitulo, String tituloObtenido, String fechaTitulo,
                        String idiomas, String certificacionesAdicionales, String experienciaLaboral) {
        this.userEmail = userEmail;
        this.tipoFormacion = tipoFormacion;
        this.institucionEducativa = institucionEducativa;
        this.programaAcademico = programaAcademico;
        this.fechaGraduacion = fechaGraduacion;
        this.numeroTarjetaProfesional = numeroTarjetaProfesional;
        this.universidadTitulo = universidadTitulo;
        this.tituloObtenido = tituloObtenido;
        this.fechaTitulo = fechaTitulo;
        this.idiomas = idiomas;
        this.certificacionesAdicionales = certificacionesAdicionales;
        this.experienciaLaboral = experienciaLaboral;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getTipoFormacion() { return tipoFormacion; }
    public void setTipoFormacion(String tipoFormacion) { this.tipoFormacion = tipoFormacion; }

    public String getInstitucionEducativa() { return institucionEducativa; }
    public void setInstitucionEducativa(String institucionEducativa) { this.institucionEducativa = institucionEducativa; }

    public String getProgramaAcademico() { return programaAcademico; }
    public void setProgramaAcademico(String programaAcademico) { this.programaAcademico = programaAcademico; }

    public String getFechaGraduacion() { return fechaGraduacion; }
    public void setFechaGraduacion(String fechaGraduacion) { this.fechaGraduacion = fechaGraduacion; }

    public String getNumeroTarjetaProfesional() { return numeroTarjetaProfesional; }
    public void setNumeroTarjetaProfesional(String numeroTarjetaProfesional) { this.numeroTarjetaProfesional = numeroTarjetaProfesional; }

    public String getUniversidadTitulo() { return universidadTitulo; }
    public void setUniversidadTitulo(String universidadTitulo) { this.universidadTitulo = universidadTitulo; }

    public String getTituloObtenido() { return tituloObtenido; }
    public void setTituloObtenido(String tituloObtenido) { this.tituloObtenido = tituloObtenido; }

    public String getFechaTitulo() { return fechaTitulo; }
    public void setFechaTitulo(String fechaTitulo) { this.fechaTitulo = fechaTitulo; }

    public String getIdiomas() { return idiomas; }
    public void setIdiomas(String idiomas) { this.idiomas = idiomas; }

    public String getCertificacionesAdicionales() { return certificacionesAdicionales; }
    public void setCertificacionesAdicionales(String certificacionesAdicionales) { this.certificacionesAdicionales = certificacionesAdicionales; }

    public String getExperienciaLaboral() { return experienciaLaboral; }
    public void setExperienciaLaboral(String experienciaLaboral) { this.experienciaLaboral = experienciaLaboral; }
}
