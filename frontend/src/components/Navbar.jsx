import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
    const { rol, correo, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const isActive = (path) =>
        location.pathname === path ? 'nav-link active' : 'nav-link';

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <nav className="navbar navbar-expand-lg bg-dark navbar-dark">
            <div className="container-fluid">
                <button className="navbar-toggler" type="button"
                        data-bs-toggle="collapse" data-bs-target="#navbarMain">
                    <span className="navbar-toggler-icon"></span>
                </button>

                <Link className="navbar-brand" to="/">BolsaEmpleo</Link>

                <div className="collapse navbar-collapse" id="navbarMain">
                    <ul className="navbar-nav me-auto mb-2 mb-lg-0">

                        {rol === 'ADMIN' && <>
                            <li className="nav-item">
                                <Link className={isActive('/admin/dashboard')} to="/admin/dashboard">Dashboard</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/admin/empresas/pendientes')} to="/admin/empresas/pendientes">Empresas pendientes</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/admin/oferentes/pendientes')} to="/admin/oferentes/pendientes">Oferentes pendientes</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/admin/caracteristicas')} to="/admin/caracteristicas">Características</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/admin/reportes')} to="/admin/reportes">Reportes</Link>
                            </li>
                        </>}

                        {rol === 'EMPRESA' && <>
                            <li className="nav-item">
                                <Link className={isActive('/empresa/dashboard')} to="/empresa/dashboard">Dashboard</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/empresa/puestos')} to="/empresa/puestos">Mis puestos</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/empresa/puestos/nuevo')} to="/empresa/puestos/nuevo">Publicar puesto</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/buscar-puestos')} to="/buscar-puestos">Ver puestos</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/empresa/reportes')} to="/empresa/reportes">Reportes</Link>
                            </li>
                        </>}

                        {rol === 'OFERENTE' && <>
                            <li className="nav-item">
                                <Link className={isActive('/oferente/dashboard')} to="/oferente/dashboard">Dashboard</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/oferente/habilidades')} to="/oferente/habilidades">Mis habilidades</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/oferente/cv')} to="/oferente/cv">Mi CV</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/buscar-puestos')} to="/buscar-puestos">Buscar puestos</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/oferente/postulacion')} to="/oferente/postulacion">Postularme</Link>
                            </li>
                            <li className="nav-item">
                                <Link className={isActive('/oferente/postulaciones')} to="/oferente/postulaciones">Mis postulaciones</Link>
                            </li>
                        </>}

                        {!rol && <>
                            <li className="nav-item">
                                <Link className={isActive('/buscar-puestos')} to="/buscar-puestos">Buscar puestos</Link>
                            </li>
                        </>}

                    </ul>

                    <div className="d-flex align-items-center gap-2">
                        {correo && (
                            <>
                                <span className="text-light me-2 small">{correo}</span>
                                <button className="btn btn-sm btn-secondary" onClick={handleLogout}>
                                    Salir
                                </button>
                            </>
                        )}
                        {!correo && (
                            <>
                                <Link className="btn btn-sm btn-outline-light me-1" to="/login">Iniciar sesión</Link>
                                <Link className="btn btn-sm btn-primary" to="/registro">Registrarse</Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}