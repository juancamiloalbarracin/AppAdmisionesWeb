import { useState } from 'react';
import { useAuth } from '../utils/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import './Login.css';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    recordar: false,
    publico: false
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));

    // Limpiar error específico cuando el usuario empiece a escribir
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    // Validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email) {
      newErrors.email = 'El correo electrónico es obligatorio';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = 'El formato del correo electrónico no es válido';
    }

    // Validar contraseña
    if (!formData.password) {
      newErrors.password = 'La contraseña es obligatoria';
    } else if (formData.password.length < 3) {
      newErrors.password = 'La contraseña debe tener al menos 3 caracteres';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const result = await login(formData.email, formData.password, formData.recordar, formData.publico);
      
      if (result.success) {
        navigate('/dashboard');
      } else {
        setErrors({ general: result.error });
      }
    } catch (error) {
      setErrors({ general: 'Error inesperado. Intente nuevamente.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-header">
        Sistema de Admisiones Uniminuto
      </div>
      
      <div className="login-content">
        <div className="login-title">
          Iniciar Sesión
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="login-field">
            <label>Correo Electrónico *</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              disabled={loading}
              className={errors.email ? 'error' : ''}
              placeholder="ejemplo@correo.com"
            />
            {errors.email && <span className="field-error">{errors.email}</span>}
          </div>

          <div className="login-field">
            <label>Contraseña *</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              disabled={loading}
              className={errors.password ? 'error' : ''}
              placeholder="Ingrese su contraseña"
            />
            {errors.password && <span className="field-error">{errors.password}</span>}
          </div>

          <div className="checkbox-group">
            <label>
              <input
                type="checkbox"
                name="recordar"
                checked={formData.recordar}
                onChange={handleChange}
                disabled={loading}
              />
              <span>Recordarme</span>
            </label>
            <label>
              <input
                type="checkbox"
                name="publico"
                checked={formData.publico}
                onChange={handleChange}
                disabled={loading}
              />
              <span>Conectado desde un PC público</span>
            </label>
          </div>

          {errors.general && (
            <div className="alert alert-error">
              {errors.general}
            </div>
          )}

          <div className="login-buttons">
            <button 
              type="submit" 
              className="btn btn-full-width"
              disabled={loading}
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
            
            <div className="login-links">
              <Link to="/forgot-password" className="forgot-link">
                ¿Olvidaste tu contraseña?
              </Link>
            </div>
            
            <div className="signup-link">
              ¿No tienes cuenta? <Link to="/signup">Crear Cuenta</Link>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
