import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getEmpresasPendientes, aprobarEmpresa } from '../../api/admin';

export default function EmpresasPendientes() {
    const { token } = useAuth();
    const [empresas, setEmpresas]   = useState([]);
    const [cargando, setCargando]   = useState(true);
    const [aprobando, setAprobando] = useState(null);

    useEffect(() => {
        getEmpresasPendientes(token)
            .then(data => setEmpresas(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    }, []);

    const handleAprobar = async (id) => {
        setAprobando(id);
        try {
            await aprobarEmpresa(id, token);
            setEmpresas(prev => prev.filter(e => e.id !== id));
        } finally {
            setAprobando(null);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Empresas pendientes</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && empresas.length === 0 && (
                    <div className="alert alert-info mt-3">
                        No hay empresas pendientes de aprobación.
                    </div>
                )}

                {!cargando && empresas.length > 0 && (
                    <table className="table table-bordered mt-3">
                        <thead className="table-dark">
                        <tr>
                            <th>Usuario</th>
                            <th>Nombre</th>
                            <th>Teléfono</th>
                            <th>Localización</th>
                            <th>Acción</th>
                        </tr>
                        </thead>
                        <tbody>
                        {empresas.map(e => (
                            <tr key={e.id}>
                                <td>{e.correo}</td>
                                <td>{e.nombre}</td>
                                <td>{e.telefono}</td>
                                <td>{e.localizacion}</td>
                                <td>
                                    <button
                                        className="btn btn-sm btn-success"
                                        disabled={aprobando === e.id}
                                        onClick={() => handleAprobar(e.id)}
                                    >
                                        {aprobando === e.id ? 'Aprobando...' : 'Aprobar'}
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