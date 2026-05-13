import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getEmpresaDashboard } from '../../api/empresa';

export default function EmpresaDashboard() {
    const { token } = useAuth();
    const [datos, setDatos]       = useState(null);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        getEmpresaDashboard(token)
            .then(data => setDatos(data))
            .finally(() => setCargando(false));
    }, []);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Empresa - Dashboard</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && datos && (
                    <p className="text-muted">
                        Bienvenido, <strong>{datos.nombre}</strong>
                    </p>
                )}

                <div className="mt-3 d-flex flex-wrap gap-2">
                    <Link to="/empresa/puestos" className="btn btn-outline-primary">
                        Ver mis puestos
                    </Link>
                    <Link to="/empresa/puestos/nuevo" className="btn btn-primary">
                        Publicar nuevo puesto
                    </Link>
                    <Link to="/empresa/reportes" className="btn btn-outline-secondary">
                        Reportes
                    </Link>
                </div>
            </main>

            <Footer />
        </div>
    );
}