import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../utils/AuthContext';
import './Login.css';

const SignUp = () => {
  const [formData, setFormData] = useState({
    nombres: '',
    apellidos: '',
    email: '',
    password: '',
    confirmPassword: '',
    tipoDocumento: 'CC',
    numeroDocumento: '',
    telefono: '',
    acceptTerms: false
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const navigate = useNavigate();
  const { register } = useAuth();

  const tiposDocumento = [
    { value: 'CC', label: 'Cédula de Ciudadanía' },
    { value: 'TI', label: 'Tarjeta de Identidad' },
    { value: 'CE', label: 'Cédula de Extranjería' },
    { value: 'PA', label: 'Pasaporte' },
    { value: 'RC', label: 'Registro Civil' }
  ];

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

    // Validar nombres
    if (!formData.nombres.trim()) {
      newErrors.nombres = 'Los nombres son obligatorios';
    } else if (formData.nombres.length < 2) {
      newErrors.nombres = 'Los nombres deben tener al menos 2 caracteres';
    }

    // Validar apellidos
    if (!formData.apellidos.trim()) {
      newErrors.apellidos = 'Los apellidos son obligatorios';
    } else if (formData.apellidos.length < 2) {
      newErrors.apellidos = 'Los apellidos deben tener al menos 2 caracteres';
    }

    // Validar email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!formData.email) {
      newErrors.email = 'El correo electrónico es obligatorio';
    } else if (!emailRegex.test(formData.email)) {
      newErrors.email = 'El formato del correo electrónico no es válido';
    }

    // Validar número de documento
    if (!formData.numeroDocumento.trim()) {
      newErrors.numeroDocumento = 'El número de documento es obligatorio';
    } else if (formData.numeroDocumento.length < 6) {
      newErrors.numeroDocumento = 'El número de documento debe tener al menos 6 caracteres';
    }

    // Validar teléfono
    const phoneRegex = /^[0-9]{10}$/;
    if (!formData.telefono) {
      newErrors.telefono = 'El teléfono es obligatorio';
    } else if (!phoneRegex.test(formData.telefono)) {
      newErrors.telefono = 'El teléfono debe tener 10 dígitos';
    }

    // Validar contraseña
    if (!formData.password) {
      newErrors.password = 'La contraseña es obligatoria';
    } else if (formData.password.length < 8) {
      newErrors.password = 'La contraseña debe tener al menos 8 caracteres';
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(formData.password)) {
      newErrors.password = 'La contraseña debe contener al menos una mayúscula, una minúscula y un número';
    }

    // Validar confirmación de contraseña
    if (!formData.confirmPassword) {
      newErrors.confirmPassword = 'Debe confirmar la contraseña';
    } else if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = 'Las contraseñas no coinciden';
    }

    // Validar términos y condiciones
    if (!formData.acceptTerms) {
      newErrors.acceptTerms = 'Debe aceptar los términos y condiciones';
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
      const result = await register({
        nombres: formData.nombres,
        apellidos: formData.apellidos,
        email: formData.email,
        password: formData.password,
        tipoDocumento: formData.tipoDocumento,
        numeroDocumento: formData.numeroDocumento,
        telefono: formData.telefono
      });

      if (result.success) {
        setSuccess(true);
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else {
        setErrors({ general: result.error });
      }
    } catch (error) {
      setErrors({ general: 'Error al crear la cuenta. Intente nuevamente.' });
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="login-container">
        <div className="login-header">
          Sistema de Admisiones Uniminuto
        </div>
        
        <div className="login-content">
          <div className="login-title">
            ¡Registro Exitoso!
          </div>
          
          <div className="alert alert-success">
            <h4>✅ Cuenta creada correctamente</h4>
            <p>Su cuenta ha sido creada exitosamente. Será redirigido al inicio de sesión en unos segundos.</p>
          </div>
          
          <Link to="/login" className="btn btn-full-width">
            Ir a Iniciar Sesión
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="login-container signup-container">
      <div className="login-header">
        Sistema de Admisiones Uniminuto
      </div>
      
      <div className="login-content signup-content">
        <div className="login-title">
          Crear Cuenta Nueva
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="signup-form-grid">
            {/* Nombres */}
            <div className="login-field">
              <label>Nombres *</label>
              <input
                type="text"
                name="nombres"
                value={formData.nombres}
                onChange={handleChange}
                disabled={loading}
                className={errors.nombres ? 'error' : ''}
              />
              {errors.nombres && <span className="field-error">{errors.nombres}</span>}
            </div>

            {/* Apellidos */}
            <div className="login-field">
              <label>Apellidos *</label>
              <input
                type="text"
                name="apellidos"
                value={formData.apellidos}
                onChange={handleChange}
                disabled={loading}
                className={errors.apellidos ? 'error' : ''}
              />
              {errors.apellidos && <span className="field-error">{errors.apellidos}</span>}
            </div>

            {/* Tipo de Documento */}
            <div className="login-field">
              <label>Tipo de Documento *</label>
              <select
                name="tipoDocumento"
                value={formData.tipoDocumento}
                onChange={handleChange}
                disabled={loading}
              >
                {tiposDocumento.map(tipo => (
                  <option key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Número de Documento */}
            <div className="login-field">
              <label>Número de Documento *</label>
              <input
                type="text"
                name="numeroDocumento"
                value={formData.numeroDocumento}
                onChange={handleChange}
                disabled={loading}
                className={errors.numeroDocumento ? 'error' : ''}
              />
              {errors.numeroDocumento && <span className="field-error">{errors.numeroDocumento}</span>}
            </div>

            {/* Email */}
            <div className="login-field full-width">
              <label>Correo Electrónico *</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                disabled={loading}
                className={errors.email ? 'error' : ''}
              />
              {errors.email && <span className="field-error">{errors.email}</span>}
            </div>

            {/* Teléfono */}
            <div className="login-field full-width">
              <label>Teléfono *</label>
              <input
                type="tel"
                name="telefono"
                value={formData.telefono}
                onChange={handleChange}
                placeholder="3001234567"
                disabled={loading}
                className={errors.telefono ? 'error' : ''}
              />
              {errors.telefono && <span className="field-error">{errors.telefono}</span>}
            </div>

            {/* Contraseña */}
            <div className="login-field">
              <label>Contraseña *</label>
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                disabled={loading}
                className={errors.password ? 'error' : ''}
              />
              {errors.password && <span className="field-error">{errors.password}</span>}
            </div>

            {/* Confirmar Contraseña */}
            <div className="login-field">
              <label>Confirmar Contraseña *</label>
              <input
                type="password"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                disabled={loading}
                className={errors.confirmPassword ? 'error' : ''}
              />
              {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
            </div>
          </div>

          {/* Términos y condiciones */}
          <div className="checkbox-group">
            <label className={errors.acceptTerms ? 'error' : ''}>
              <input
                type="checkbox"
                name="acceptTerms"
                checked={formData.acceptTerms}
                onChange={handleChange}
                disabled={loading}
              />
              <span>
                Acepto los <Link to="/terminos" target="_blank">términos y condiciones</Link> y la <Link to="/privacidad" target="_blank">política de privacidad</Link> *
              </span>
            </label>
            {errors.acceptTerms && <span className="field-error">{errors.acceptTerms}</span>}
          </div>

          {/* Error general */}
          {errors.general && (
            <div className="alert alert-error">
              {errors.general}
            </div>
          )}

          {/* Botones */}
          <div className="signup-buttons">
            <button 
              type="submit" 
              className="btn btn-full-width"
              disabled={loading}
            >
              {loading ? 'Creando cuenta...' : 'Crear Cuenta'}
            </button>
            
            <div className="signup-login-link">
              ¿Ya tienes cuenta? <Link to="/login">Iniciar Sesión</Link>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
};

export default SignUp;
