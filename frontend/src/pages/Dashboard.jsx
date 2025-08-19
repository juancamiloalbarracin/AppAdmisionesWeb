import { useAuth } from '../utils/AuthContext';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useAuth();

  return (
    <div className="dashboard-container">
      {/* Cards horizontales como en MainView.java */}
      <div className="info-cards">
        <div className="info-card">
          <h3>Información Académica</h3>
          <p>Gestiona tu información académica, incluyendo programas de estudio, calificaciones y historial académico. Mantén actualizada tu información para un mejor seguimiento de tu progreso educativo.</p>
        </div>
        
        <div className="info-card">
          <h3>Información Personal</h3>
          <p>Actualiza y mantén tu información personal como datos de contacto, dirección, información familiar y de emergencia. Es importante mantener estos datos actualizados para una comunicación efectiva.</p>
        </div>
        
        <div className="info-card">
          <h3>Radicar Solicitud</h3>
          <p>Presenta solicitudes oficiales ante la institución. Puedes radicar diferentes tipos de solicitudes administrativas y dar seguimiento a su estado de procesamiento.</p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
