import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getOferenteDashboard } from '../../api/oferente';

export default function OferenteDashboard() {
    const { token } = useAuth();
    const [datos, setDatos]       = useState(null);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        getOferenteDashboard(token)
            .then(data => setDatos(data))
            .finally(() => setCargando(false));
    }, []);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Oferente - Dashboard</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && datos && (
                    <p className="text-muted">
                        Bienvenido, <strong>{datos.nombre} {datos.primerApellido}</strong>
                    </p>
                )}

                <div className="mt-3 d-flex flex-wrap gap-2">
                    <Link to="/oferente/habilidades" className="btn btn-primary">
                        Mis habilidades
                    </Link>
                    <Link to="/oferente/cv" className="btn btn-primary">
                        Mi CV
                    </Link>
                    <Link to="/oferente/postulacion" className="btn btn-outline-primary">
                        Postularme
                    </Link>
                    <Link to="/oferente/postulaciones" className="btn btn-outline-primary">
                        Mis postulaciones
                    </Link>
                    <Link to="/buscar-puestos" className="btn btn-outline-secondary">
                        Buscar puestos
                    </Link>
                </div>
            </main>

            <Footer />
        </div>
    );
}