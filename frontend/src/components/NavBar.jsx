import { useAuth } from '../utils/AuthContext';
import { useNavigate } from 'react-router-dom';
import './NavBar.css';

const NavBar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="app-navigation">
      <button 
        className="nav-button"
        onClick={() => navigate('/info-academica')}
      >
        Información Académica
      </button>
      <button 
        className="nav-button"
        onClick={() => navigate('/info-personal')}
      >
        Información Personal
      </button>
      <button 
        className="nav-button"
        onClick={() => navigate('/radicar')}
      >
        Radicar Solicitud
      </button>
    </nav>
  );
};

export default NavBar;
