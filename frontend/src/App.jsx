import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider } from './utils/AuthContext';
import InfoPersonalFixed from './pages/InfoPersonal-Fixed';
import InfoAcademicaComplete from './pages/InfoAcademica-Complete';
import RadicarComplete from './pages/Radicar-Complete';
import Login from './pages/Login';
import SignUp from './pages/SignUp';
import ResumenPerfil from './pages/ResumenPerfil';
import './App.css';

// Dashboard MEJORADO - centrado y responsivo
function RealDashboard() {
  return (
    <div className="dashboard-container">
      <div className="dashboard-wrapper">
        {/* Header fijo */}
        <div className="dashboard-header">
          <h1>Sistema de Admisiones Uniminuto</h1>
          <p>Portal de Gestión Académica</p>
        </div>

        {/* Contenido principal */}
        <div className="dashboard-content">
          {/* Tarjetas de navegación */}
          <div className="dashboard-cards">
            <Link to="/resumen" className="dashboard-card">
              <div className="card-icon">📊</div>
              <div className="card-content">
                <h3>Mi Resumen</h3>
                <p>Consulta tu información personal, académica y solicitudes en un solo lugar.</p>
                <div className="card-arrow">→</div>
              </div>
            </Link>
            <Link to="/info-personal" className="dashboard-card">
              <div className="card-icon">👤</div>
              <div className="card-content">
                <h3>Información Personal</h3>
                <p>Complete y gestione sus datos personales, información de contacto y datos del acudiente</p>
                <div className="card-arrow">→</div>
              </div>
            </Link>
            
            <Link to="/info-academica" className="dashboard-card">
              <div className="card-icon">🎓</div>
              <div className="card-content">
                <h3>Información Académica</h3>
                <p>Registre su información académica, nivel educativo, sede y programa de interés</p>
                <div className="card-arrow">→</div>
              </div>
            </Link>
            
            <Link to="/radicar" className="dashboard-card">
              <div className="card-icon">📄</div>
              <div className="card-content">
                <h3>Radicar Solicitud</h3>
                <p>Presente su solicitud oficial de admisión con documentos y términos requeridos</p>
                <div className="card-arrow">→</div>
              </div>
            </Link>
          </div>

          {/* Información adicional */}
          <div className="dashboard-info">
            <div className="info-section">
              <h4>📋 Estado de su proceso</h4>
              <p>Complete toda la información requerida para proceder con su solicitud de admisión.</p>
              <div className="progress-indicators">
                <div className="progress-item">
                  <span className="progress-dot pending">1</span>
                  <span>Información Personal</span>
                </div>
                <div className="progress-item">
                  <span className="progress-dot pending">2</span>
                  <span>Información Académica</span>
                </div>
                <div className="progress-item">
                  <span className="progress-dot pending">3</span>
                  <span>Radicar Solicitud</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer con navegación rápida */}
        <div className="dashboard-footer">
          <Link to="/login" className="footer-link">
            🔐 Cerrar Sesión
          </Link>
          <span className="footer-divider">|</span>
          <Link to="/login" className="footer-link">
            ⚙️ Configuración
          </Link>
        </div>
      </div>
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            <Route path="/" element={<RealDashboard />} />
            <Route path="/dashboard" element={<RealDashboard />} />
            <Route path="/login" element={<Login />} />
            <Route path="/resumen" element={<ResumenPerfil />} />
            <Route path="/signup" element={<SignUp />} />
            <Route path="/info-personal" element={<InfoPersonalFixed />} />
            <Route path="/info-academica" element={<InfoAcademicaComplete />} />
            <Route path="/radicar" element={<RadicarComplete />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
