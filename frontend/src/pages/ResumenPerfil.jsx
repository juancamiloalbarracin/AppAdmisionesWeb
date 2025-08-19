import React, { useEffect, useState } from 'react';
import { infoPersonalService, infoAcademicaService, solicitudesService } from '../services/api';
import './Dashboard.css';

const Section = ({ title, children }) => (
  <div className="resumen-section">
    <h3>{title}</h3>
    <div className="resumen-card">
      {children}
    </div>
  </div>
);

export default function ResumenPerfil() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [personal, setPersonal] = useState(null);
  const [academica, setAcademica] = useState(null);
  const [solicitudes, setSolicitudes] = useState([]);

  useEffect(() => {
    const load = async () => {
      try {
        setLoading(true);
        const [p, a, s] = await Promise.all([
          infoPersonalService.getInfo(),
          infoAcademicaService.getInfo(),
          solicitudesService.getSolicitudes()
        ]);
        setPersonal(p?.data || {});
        setAcademica(a?.data || {});
        setSolicitudes(s?.data || []);
      } catch (e) {
        setError(e?.message || 'Error cargando el resumen');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div className="dashboard-container"><div className="dashboard-wrapper"><div className="login-title">Cargando...</div></div></div>;
  if (error) return <div className="dashboard-container"><div className="dashboard-wrapper"><div className="alert alert-error">{error}</div></div></div>;

  return (
    <div className="dashboard-container">
      <div className="dashboard-wrapper">
        <div className="dashboard-header">
          <h1>Sistema de Admisiones Uniminuto</h1>
          <p>Resumen de tu perfil</p>
        </div>

        <div className="dashboard-content">
          <div className="dashboard-cards">
            <a href="#personal" className="dashboard-card">
              <div className="card-icon">👤</div>
              <div className="card-content">
                <h3>Información Personal</h3>
                <p>Nombre: {(personal?.nombres||'') + ' ' + (personal?.apellidos||'')}</p>
                <div className="card-arrow">→</div>
              </div>
            </a>

            <a href="#academica" className="dashboard-card">
              <div className="card-icon">🎓</div>
              <div className="card-content">
                <h3>Información Académica</h3>
                <p>Institución: {academica?.institucionBachillerato || academica?.nivel || '—'}</p>
                <div className="card-arrow">→</div>
              </div>
            </a>

            <a href="#solicitudes" className="dashboard-card">
              <div className="card-icon">📄</div>
              <div className="card-content">
                <h3>Mis Solicitudes</h3>
                <p>Total radicadas: {Array.isArray(solicitudes) ? solicitudes.length : 0}</p>
                <div className="card-arrow">→</div>
              </div>
            </a>
          </div>

          <div id="personal" className="resumen-sections">
            <Section title="Información Personal">
              <div className="resumen-grid">
                <div><strong>Nombres:</strong> {personal?.nombres || '—'}</div>
                <div><strong>Apellidos:</strong> {personal?.apellidos || '—'}</div>
                <div><strong>Documento:</strong> {(personal?.tipoDocumento||'') + ' ' + (personal?.numeroDocumento||'')}</div>
                <div><strong>Teléfono:</strong> {personal?.telefono || '—'}</div>
                <div><strong>Dirección:</strong> {personal?.direccion || '—'}</div>
                <div><strong>Correo personal:</strong> {personal?.emailPersonal || '—'}</div>
              </div>
            </Section>
          </div>

          <div id="academica" className="resumen-sections">
            <Section title="Información Académica">
              <div className="resumen-grid">
                <div><strong>Institución:</strong> {academica?.institucionBachillerato || academica?.nivel || '—'}</div>
                <div><strong>Año Bachillerato / Periodo:</strong> {academica?.anioBachillerato || academica?.periodoAdmision || '—'}</div>
                <div><strong>Promedio:</strong> {academica?.promedioAcademico || academica?.gradoAcademico || '—'}</div>
                <div><strong>Modalidad:</strong> {academica?.modalidadEstudio || academica?.metodologia || '—'}</div>
                <div><strong>Pruebas Estado:</strong> {academica?.pruebasEstado || academica?.planDecision || '—'}</div>
                <div><strong>Certificaciones:</strong> {academica?.certificacionesAdicionales || academica?.gradoObtenido || '—'}</div>
              </div>
            </Section>
          </div>

          <div id="solicitudes" className="resumen-sections">
            <Section title="Solicitudes Radicadas">
              {Array.isArray(solicitudes) && solicitudes.length > 0 ? (
                <div className="resumen-table">
                  <div className="resumen-thead">
                    <div>Número</div>
                    <div>Tipo</div>
                    <div>Asunto</div>
                    <div>Fecha</div>
                    <div>Estado</div>
                  </div>
                  {solicitudes.map((s, idx) => (
                    <div key={idx} className="resumen-row">
                      <div>{s.numeroRadicado || s.numero_radicado || '—'}</div>
                      <div>{s.tipoSolicitud || s.tipo_solicitud || '—'}</div>
                      <div>{s.asunto || '—'}</div>
                      <div>{s.fechaRadicacion || s.fecha_radicado || '—'}</div>
                      <div>{s.estado || 'Radicada'}</div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center">No tienes solicitudes registradas.</div>
              )}
            </Section>
          </div>
        </div>
      </div>
    </div>
  );
}
