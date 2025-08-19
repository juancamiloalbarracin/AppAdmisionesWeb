import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { infoAcademicaService } from '../services/api';
import './InfoPersonal.css';

// Componente para títulos de sección
const SectionTitle = ({ children }) => (
  <div className="section-title">
    {children}
  </div>
);

// Componente para campos del formulario
const FormField = ({ label, type = "text", options, ...props }) => (
  <div className="form-field">
    <label>{label}:</label>
    {type === "select" ? (
      <select {...props}>
        <option value="">Seleccione...</option>
        {options?.map((option, index) => (
          <option key={index} value={option.value || option}>
            {option.label || option}
          </option>
        ))}
      </select>
    ) : (
      <input type={type} {...props} />
    )}
  </div>
);

function InfoAcademicaComplete() {
  const [formData, setFormData] = useState({
    // Campos exactos de InfoAcademicaView.java
    nivel: '',
    sede: '',
    gradoAcademico: '',
    periodoAdmision: '',
    metodologia: '',
    jornada: '',
    planDecision: '',
    gradoSeleccionado: '',
    pais: '',
    gradoObtenido: '',
    fechaGraduacion: ''
  });

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    try {
      const resp = await infoAcademicaService.saveInfo(formData);
      if (resp?.success) {
        setMessage('Información académica guardada exitosamente');
      } else {
        setMessage(resp?.message || 'No se pudo guardar');
      }
    } catch (_) {
      setMessage('Error al guardar la información');
    } finally {
      setSaving(false);
    }
  };

  // Opciones exactas de Java Swing
  const niveles = ["Primaria", "Secundaria", "Cursos complementarios"];
  
  const sedes = [
    "Apartado", "Arauca", "Armenia", "Barrancabermeja", "Barranquilla", 
    "Bogota D.C", "Boyaca", "Bucaramanga", "Buga", "Cali", "Cartagena", 
    "Casanare", "Caqueta"
  ];

  const grados = [
    "1 Primaria", "2 Primaria", "3 Primaria", "4 Primaria", "5 Primaria",
    "6 Bachillerato", "7 Bachillerato", "8 Bachillerato", "9 Bachillerato", 
    "10 Bachillerato", "11 Bachillerato", "Cursos complementarios"
  ];

  const jornadas = ["Diurna", "Nocturna"];
  const planes = ["Plan A", "Plan B", "Plan C"];

  return (
    <div className="info-personal-container">
      <div className="info-personal-wrapper">
        {/* Header fijo */}
        <div className="info-personal-header">
          <h2>Información Académica</h2>
        </div>

        {/* Contenido con scroll */}
        <div className="info-personal-content">
          {/* Instrucciones exactas de Java */}
          <div style={{
            backgroundColor: '#E6E6FA',
            border: '1px solid #00264C',
            padding: '15px',
            marginBottom: '25px',
            fontSize: '14px',
            lineHeight: '1.4',
            borderRadius: '5px'
          }}>
            Ingrese su información en cada una de las siguientes secciones. Los campos con un asterisco (*) rojo son obligatorios 
            y deben completarse para enviar la solicitud. Pueden ser requeridas preguntas adicionales en base a sus respuestas a una pregunta anterior.
          </div>

          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              
              {/* Fila 1: Nivel y Sede */}
              <FormField 
                label="Nivel *" 
                name="nivel"
                type="select"
                value={formData.nivel}
                onChange={handleChange}
                options={niveles}
                required
              />
              <FormField 
                label="Sede *" 
                name="sede"
                type="select"
                value={formData.sede}
                onChange={handleChange}
                options={sedes}
                required
              />

              {/* Fila 2: Grado académico y Período de admisión */}
              <FormField 
                label="Grado académico *" 
                name="gradoAcademico"
                type="select"
                value={formData.gradoAcademico}
                onChange={handleChange}
                options={grados}
                required
              />
              <FormField 
                label="Período de admisión *" 
                name="periodoAdmision"
                value={formData.periodoAdmision}
                onChange={handleChange}
                required
              />

              {/* Fila 3: Metodología y Jornada */}
              <FormField 
                label="Metodología" 
                name="metodologia"
                value={formData.metodologia}
                onChange={handleChange}
              />
              <FormField 
                label="Jornada *" 
                name="jornada"
                type="select"
                value={formData.jornada}
                onChange={handleChange}
                options={jornadas}
                required
              />

              {/* Fila 4: Plan de decisión y Grado seleccionado */}
              <FormField 
                label="Plan de decisión *" 
                name="planDecision"
                type="select"
                value={formData.planDecision}
                onChange={handleChange}
                options={planes}
                required
              />
              <FormField 
                label="Grado seleccionado" 
                name="gradoSeleccionado"
                value={formData.gradoSeleccionado}
                onChange={handleChange}
              />

              {/* Fila 5: País y Grado obtenido */}
              <FormField 
                label="País *" 
                name="pais"
                value={formData.pais}
                onChange={handleChange}
                required
              />
              <FormField 
                label="Grado obtenido *" 
                name="gradoObtenido"
                value={formData.gradoObtenido}
                onChange={handleChange}
                required
              />

              {/* Fila 6: Fecha de graduación - ocupa 2 columnas */}
              <div className="form-field form-field-half-width">
                <label>Fecha de graduación (DD/MM/AAAA):</label>
                <input 
                  type="date"
                  name="fechaGraduacion"
                  value={formData.fechaGraduacion}
                  onChange={handleChange}
                />
              </div>

              {/* Botones */}
              <div className="form-actions">
                {message && (
                  <div className="info-banner" style={{ marginBottom: '12px', color: '#00264C' }}>{message}</div>
                )}
                <button type="submit" className="btn-primary" disabled={saving}>
                  {saving ? 'Guardando...' : 'Guardar Información Académica'}
                </button>
                <Link to="/" className="btn-secondary">
                  ← Volver al Dashboard
                </Link>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

export default InfoAcademicaComplete;
