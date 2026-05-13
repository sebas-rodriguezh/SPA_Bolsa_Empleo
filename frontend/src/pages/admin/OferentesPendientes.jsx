import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getOferentesPendientes, aprobarOferente } from '../../api/admin';

export default function OferentesPendientes() {
    const { token } = useAuth();
    const [oferentes, setOferentes] = useState([]);
    const [cargando, setCargando]   = useState(true);
    const [aprobando, setAprobando] = useState(null);

    useEffect(() => {
        getOferentesPendientes(token)
            .then(data => setOferentes(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    }, []);

    const handleAprobar = async (id) => {
        setAprobando(id);
        try {
            await aprobarOferente(id, token);
            setOferentes(prev => prev.filter(o => o.id !== id));
        } finally {
            setAprobando(null);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Oferentes pendientes</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && oferentes.length === 0 && (
                    <div className="alert alert-info mt-3">
                        No hay oferentes pendientes de aprobación.
                    </div>
                )}

                {!cargando && oferentes.length > 0 && (
                    <table className="table table-bordered mt-3">
                        <thead className="table-dark">
                        <tr>
                            <th>Usuario</th>
                            <th>Nombre</th>
                            <th>Identificación</th>
                            <th>Teléfono</th>
                            <th>Localización</th>
                            <th>Acción</th>
                        </tr>
                        </thead>
                        <tbody>
                        {oferentes.map(o => (
                            <tr key={o.id}>
                                <td>{o.correo}</td>
                                <td>{o.nombre}</td>
                                <td>{o.identificacion}</td>
                                <td>{o.telefono}</td>
                                <td>{o.lugarResidencia}</td>
                                <td>
                                    <button
                                        className="btn btn-sm btn-success"
                                        disabled={aprobando === o.id}
                                        onClick={() => handleAprobar(o.id)}
                                    >
                                        {aprobando === o.id ? 'Aprobando...' : 'Aprobar'}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </main>

            <Footer />
        </div>
    );
}