import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { solicitudesService } from '../services/api';
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
    ) : type === "textarea" ? (
      <textarea 
        {...props}
        rows={6}
        style={{ 
          resize: 'vertical', 
          minHeight: '120px',
          fontFamily: 'inherit'
        }}
      />
    ) : (
      <input type={type} {...props} />
    )}
  </div>
);

function RadicarComplete() {
  const [formData, setFormData] = useState({
    // Campos exactos de RadicarView.java
    tipoSolicitud: '',
    asunto: '',
    descripcion: '',
    telefonoContacto: '',
    emailNotificacion: '',
    terminosAceptados: false
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : value
    });
  };

  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.terminosAceptados) {
      alert('Debe aceptar los términos y condiciones');
      return;
    }
    setSaving(true);
    setMessage('');
    // Mapear UI -> backend keys
    const payload = {
      tipo_solicitud: formData.tipoSolicitud,
      asunto: formData.asunto,
      descripcion: formData.descripcion,
      telefono_contacto: formData.telefonoContacto,
      email_notificacion: formData.emailNotificacion
    };
    try {
      const resp = await solicitudesService.radicarSolicitud(payload);
      if (resp?.success) {
        setMessage('Solicitud radicada exitosamente. Número: ' + (resp.numeroRadicado || '—'));
      } else {
        setMessage(resp?.message || 'No se pudo radicar');
      }
    } catch (_) {
      setMessage('Error al radicar la solicitud');
    } finally {
      setSaving(false);
    }
  };

  // Opciones exactas de Java Swing
  const tiposSolicitud = [
    "Certificado de Estudios",
    "Constancia de Matrícula",
    "Solicitud de Traslado",
    "Solicitud de Reingreso",
    "Certificado de Notas",
    "Solicitud de Grado",
    "Cambio de Programa",
    "Solicitud de Homologación",
    "Retiro Temporal",
    "Otros"
  ];

  return (
    <div className="info-personal-container">
      <div className="info-personal-wrapper">
        {/* Header fijo */}
        <div className="info-personal-header">
          <h2>Radicar Solicitud</h2>
        </div>

        {/* Contenido con scroll */}
        <div className="info-personal-content">
          
          {/* Información importante - texto exacto de Java */}
          <div style={{
            backgroundColor: '#f0f0f0',
            border: '1px solid #ccc',
            padding: '15px',
            marginBottom: '25px',
            fontSize: '12px',
            lineHeight: '1.5',
            borderRadius: '5px',
            maxHeight: '200px',
            overflowY: 'auto'
          }}>
            <h4 style={{ margin: '0 0 10px 0', color: '#00264C' }}>Información Importante y Términos de Solicitud</h4>
            <p>Por favor tenga en cuenta que, una vez enviada la presente solicitud, la misma no podrá ser modificada.</p>
            <p>Por lo tanto, es importante que la lea con detenimiento y confirme la veracidad de la información antes de su envío.</p>
            <p>Una vez se envíe con éxito, recibirá un mensaje de confirmación, con la correspondiente actualización del estatus del envío de su solicitud en la pestaña "mis solicitudes".</p>
            <p>Recuerde que para que su solicitud de admisión sea tramitada, debe adjuntar los documentos requeridos de manera completa y los mismos deben ser legibles.</p>
            
            <h5 style={{ margin: '15px 0 5px 0', color: '#00264C' }}>--- Certificación ---</h5>
            <p>Antes de enviar su solicitud usted manifiesta:</p>
            <ul style={{ fontSize: '11px', paddingLeft: '20px' }}>
              <li>Certifico que toda la información suministrada en el presente formulario es correcta y veraz; y entiendo que, una vez enviada la solicitud, no es posible modificar la información diligenciada.</li>
              <li>Certifico y entiendo que UNIMINUTO no está obligado a aceptar la solicitud con el simple registro de mis datos en el presente formulario y que la admisión está condicionada a la verificación del cumplimiento de los requisitos legales e institucionales.</li>
              <li>Entiendo y acepto que, en caso de que la información aportada no corresponda a la realidad, UNIMINUTO revocará la admisión e iniciará las investigaciones disciplinarias o legales que apliquen.</li>
              <li>Con la aceptación de la presente solicitud, declaro de manera libre y voluntaria que me encuentro afiliado al Sistema General de Seguridad Social en Salud y me comprometo a mantener vigente mi afiliación durante la duración de todo el periodo académico al cual me matriculo.</li>
            </ul>

            <h5 style={{ margin: '15px 0 5px 0', color: '#00264C' }}>--- Contrato de Matrícula * ---</h5>
            <p style={{ fontSize: '11px' }}>Declaro en forma libre y voluntaria que he leído, he comprendido y por lo tanto acepto todas y cada una de las estipulaciones contenidas en el presente contrato matrícula y los demás documentos que forman parte integral del mismo, para el período académico al cual me estoy postulando.</p>
          </div>

          <form onSubmit={handleSubmit}>
            <div className="form-grid">
              
              {/* Tipo de solicitud - ocupa 2 columnas */}
              <div className="form-field form-field-half-width">
                <label>Tipo de Solicitud *:</label>
                <select 
                  name="tipoSolicitud"
                  value={formData.tipoSolicitud}
                  onChange={handleChange}
                  required
                >
                  <option value="">Seleccione tipo de solicitud...</option>
                  {tiposSolicitud.map((tipo, index) => (
                    <option key={index} value={tipo}>{tipo}</option>
                  ))}
                </select>
              </div>

              {/* Asunto - ocupa 2 columnas */}
              <div className="form-field form-field-half-width">
                <label>Asunto *:</label>
                <input 
                  type="text"
                  name="asunto"
                  value={formData.asunto}
                  onChange={handleChange}
                  required
                />
              </div>

              {/* Descripción - ocupa todo el ancho */}
              <div className="form-field form-field-full-width">
                <label>Descripción/Justificación *:</label>
                <textarea 
                  name="descripcion"
                  value={formData.descripcion}
                  onChange={handleChange}
                  placeholder="Detalle su solicitud"
                  required
                />
              </div>

              {/* Información de contacto */}
              <FormField 
                label="Teléfono de Contacto" 
                name="telefonoContacto"
                type="tel"
                value={formData.telefonoContacto}
                onChange={handleChange}
              />
              <FormField 
                label="Email de Notificación" 
                name="emailNotificacion"
                type="email"
                value={formData.emailNotificacion}
                onChange={handleChange}
              />

              {/* Documentos adjuntos - placeholder */}
              <div className="form-field form-field-half-width">
                <label>Documentos Adjuntos:</label>
                <div style={{
                  border: '2px dashed #d1d5db',
                  borderRadius: '6px',
                  padding: '20px',
                  textAlign: 'center',
                  color: '#6b7280',
                  fontSize: '14px'
                }}>
                  <button 
                    type="button"
                    style={{
                      background: '#00264C',
                      color: 'white',
                      border: 'none',
                      padding: '8px 16px',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      marginBottom: '8px'
                    }}
                  >
                    Seleccionar Archivos
                  </button>
                  <br />
                  <span style={{ fontSize: '12px', fontStyle: 'italic' }}>
                    Ningún archivo seleccionado
                  </span>
                </div>
              </div>

              {/* Checkbox términos - ocupa todo el ancho */}
              <div className="form-field form-field-full-width">
                <label style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '10px',
                  cursor: 'pointer'
                }}>
                  <input 
                    type="checkbox"
                    name="terminosAceptados"
                    checked={formData.terminosAceptados}
                    onChange={handleChange}
                    required
                    style={{ transform: 'scale(1.2)' }}
                  />
                  <span>Acepto los términos y condiciones para el procesamiento de solicitudes *</span>
                </label>
              </div>

              {/* Botones */}
              <div className="form-actions">
                {message && (
                  <div className="info-banner" style={{ marginBottom: '12px', color: '#00264C' }}>{message}</div>
                )}
                <button type="submit" className="btn-primary" disabled={saving}>
                  {saving ? 'Radicando...' : 'Radicar Solicitud'}
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

export default RadicarComplete;
