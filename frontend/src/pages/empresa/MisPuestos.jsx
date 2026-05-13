import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getMisPuestos, desactivarPuesto } from '../../api/empresa';

function formatSalario(salario, moneda) {
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${Number(salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}

export default function MisPuestos() {
    const { token } = useAuth();
    const [puestos, setPuestos]         = useState([]);
    const [cargando, setCargando]       = useState(true);
    const [desactivando, setDesactivando] = useState(null);

    useEffect(() => {
        getMisPuestos(token)
            .then(data => setPuestos(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    }, []);

    const handleDesactivar = async (id) => {
        setDesactivando(id);
        try {
            await desactivarPuesto(id, token);
            setPuestos(prev => prev.map(p =>
                p.id === id ? { ...p, activo: false } : p
            ));
        } finally {
            setDesactivando(null);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4 className="mb-3">Mis puestos</h4>

                <Link to="/empresa/puestos/nuevo" className="btn btn-primary btn-sm mb-3">
                    Publicar puesto
                </Link>

                {cargando && <LoadingSpinner />}

                {!cargando && puestos.length === 0 && (
                    <div className="alert alert-info mt-3">
                        No tenés puestos publicados aún.{' '}
                        <Link to="/empresa/puestos/nuevo" className="alert-link">
                            Publicar uno ahora.
                        </Link>
                    </div>
                )}

                {!cargando && puestos.length > 0 && (
                    <table className="table table-bordered table-hover">
                        <thead className="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Salario</th>
                            <th>Activo</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {puestos.map(p => (
                            <tr key={p.id}>
                                <td>{p.id}</td>
                                <td>{p.nombre}</td>
                                <td>{formatSalario(p.salario, p.moneda)}</td>
                                <td>
                                    {p.activo
                                        ? <span className="badge bg-success">Sí</span>
                                        : <span className="badge bg-secondary">No</span>
                                    }
                                </td>
                                <td className="d-flex flex-wrap gap-1">
                                    {p.activo && (
                                        <button
                                            className="btn btn-sm btn-outline-danger"
                                            disabled={desactivando === p.id}
                                            onClick={() => handleDesactivar(p.id)}
                                        >
                                            {desactivando === p.id ? 'Desactivando...' : 'Desactivar'}
                                        </button>
                                    )}
                                    {p.activo && (
                                        <Link
                                            to={`/empresa/puestos/${p.id}/candidatos`}
                                            className="btn btn-sm btn-primary"
                                        >
                                            Buscar candidatos
                                        </Link>
                                    )}
                                    <Link
                                        to={`/empresa/puestos/${p.id}/postulaciones`}
                                        className="btn btn-sm btn-outline-primary"
                                    >
                                        Ver postulaciones
                                    </Link>
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