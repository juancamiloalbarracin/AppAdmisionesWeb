import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Verificar si hay un token guardado al iniciar la aplicación
    const checkAuth = async () => {
      try {
        if (authService.isAuthenticated()) {
          const userInfo = authService.getUserInfo();
          if (userInfo) {
            setUser(userInfo);
            setIsAuthenticated(true);
          }
          // Por ahora no validamos con el servidor para evitar errores
          // TODO: Reactivar validación del servidor cuando todo funcione
          // else {
          //   const profile = await authService.getProfile();
          //   setUser(profile.user);
          //   setIsAuthenticated(true);
          // }
        }
      } catch (error) {
        console.error('Error al verificar autenticación:', error);
        // Limpiar datos si hay error
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user_info');
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = async (email, password, recordar = false, publico = false) => {
    try {
      const response = await authService.login(email, password, recordar, publico);
      setUser(response.user);
      setIsAuthenticated(true);
      return { success: true, data: response };
    } catch (error) {
      console.error('Error en login:', error);
      return { 
        success: false, 
        error: error.message || 'Error de autenticación' 
      };
    }
  };

  const register = async (userData) => {
    try {
      const response = await authService.register(userData);
      return { success: true, data: response };
    } catch (error) {
      console.error('Error en registro:', error);
      return { 
        success: false, 
        error: error.message || 'Error al crear la cuenta' 
      };
    }
  };

  const logout = async () => {
    try {
      await authService.logout();
    } catch (error) {
      console.error('Error en logout:', error);
    } finally {
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    register,
    logout
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
