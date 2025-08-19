import axios from 'axios';

// Configuración base de la API (usar proxy de Vite en dev)
// En dev, Vite proxy redirige /api -> http://localhost:8080
const API_BASE_URL = '/';

// Crear instancia de axios con configuración base
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  // Enviar cookies de sesión para que el backend valide HttpSession
  withCredentials: true,
});

// Interceptor para añadir el token JWT a todas las peticiones
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar respuestas y errores
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido, limpiar storage y redirigir al login
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Servicios de Autenticación
export const authService = {
  login: async (email, password, recordar = false, publico = false) => {
    try {
      const response = await api.post('/api/auth/login', { 
        email, 
        password, 
        recordarme: recordar,
        pcPublico: publico 
      });
      
      const { token, user, sessionId } = response.data;
      
      // Guardar datos en localStorage
      localStorage.setItem('jwt_token', token);
      localStorage.setItem('user_info', JSON.stringify(user));
      localStorage.setItem('session_id', sessionId);
      
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Error al iniciar sesión');
    }
  },

  register: async (userData) => {
    try {
      // Endpoint oficial del backend
      const response = await api.post('/api/users/register', {
        nombres: userData.nombres,
        apellidos: userData.apellidos,
        email: userData.email,
        password: userData.password,
        tipoDocumento: userData.tipoDocumento,
        numeroDocumento: userData.numeroDocumento,
        telefono: userData.telefono
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.error || 'Error al crear la cuenta');
    }
  },

  logout: async () => {
    try {
      await api.post('/api/auth/logout');
    } finally {
      localStorage.removeItem('jwt_token');
      localStorage.removeItem('user_info');
      localStorage.removeItem('session_id');
    }
  },

  getProfile: async () => {
    const response = await api.get('/api/auth/profile');
    return response.data;
  },

  isAuthenticated: () => {
    return localStorage.getItem('jwt_token') !== null;
  },

  getUserInfo: () => {
    const userInfo = localStorage.getItem('user_info');
    return userInfo ? JSON.parse(userInfo) : null;
  }
};

// Servicios de Información Personal
export const infoPersonalService = {
  getInfo: async () => {
    const response = await api.get('/api/info-personal/get');
    return response.data;
  },

  saveInfo: async (infoData) => {
    const response = await api.post('/api/info-personal/save', infoData);
    return response.data;
  }
};

// Servicios de Información Académica
export const infoAcademicaService = {
  getInfo: async () => {
    const response = await api.get('/api/info-academica/get');
    return response.data;
  },

  saveInfo: async (infoData) => {
    const response = await api.post('/api/info-academica/save', infoData);
    return response.data;
  }
};

// Servicios de Solicitudes
export const solicitudesService = {
  getSolicitudes: async () => {
    const response = await api.get('/api/solicitudes/list');
    return response.data;
  },

  radicarSolicitud: async (solicitudData) => {
    const response = await api.post('/api/solicitudes/radicar', solicitudData);
    return response.data;
  }
};

export default api;
