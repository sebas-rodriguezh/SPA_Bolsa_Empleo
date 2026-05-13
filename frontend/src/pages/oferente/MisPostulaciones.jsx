import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getMisPostulaciones } from '../../api/oferente';

function formatSalario(salario, moneda) {
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${Number(salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}

function formatFecha(fecha) {
    if (!fecha) return '';
    const [y, m, d] = fecha.split('-');
    return `${d}/${m}/${y}`;
}

export default function MisPostulaciones() {
    const { token } = useAuth();
    const [postulaciones, setPostulaciones] = useState([]);
    const [cargando, setCargando]           = useState(true);

    useEffect(() => {
        getMisPostulaciones(token)
            .then(data => setPostulaciones(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    }, []);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Mis postulaciones</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && postulaciones.length === 0 && (
                    <div className="alert alert-info mt-3">
                        Aún no has aplicado a ningún puesto.
                    </div>
                )}

                {!cargando && postulaciones.length > 0 && (
                    <table className="table table-bordered table-hover mt-3">
                        <thead className="table-dark">
                        <tr>
                            <th>Puesto</th>
                            <th>Empresa</th>
                            <th>Salario</th>
                            <th>Fecha postulación</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody>
                        {postulaciones.map(p => (
                            <tr key={p.id}>
                                <td>{p.puestoNombre}</td>
                                <td>{p.empresaNombre}</td>
                                <td>{formatSalario(p.salario, p.moneda)}</td>
                                <td>{formatFecha(p.fechaPostulacion)}</td>
                                <td>
                                        <span className={`badge ${p.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 'bg-success'}`}>
                                            {p.estado}
                                        </span>
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