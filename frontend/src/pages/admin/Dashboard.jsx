import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';

export default function AdminDashboard() {
    const { correo } = useAuth();

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Administrador</h4>
                <p className="text-muted">Bienvenido, {correo}</p>

                <div className="mt-3 d-flex flex-wrap gap-2">
                    <Link to="/admin/empresas/pendientes" className="btn btn-primary">
                        Empresas pendientes
                    </Link>
                    <Link to="/admin/oferentes/pendientes" className="btn btn-primary">
                        Oferentes pendientes
                    </Link>
                    <Link to="/admin/caracteristicas" className="btn btn-primary">
                        Características
                    </Link>
                    <Link to="/admin/reportes" className="btn btn-primary">
                        Reportes
                    </Link>
                </div>
            </main>

            <Footer />
        </div>
    );
}