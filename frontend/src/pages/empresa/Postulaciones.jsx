import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getPostulaciones } from '../../api/empresa';

function formatFecha(fecha) {
    if (!fecha) return '';
    const [y, m, d] = fecha.split('-');
    return `${d}/${m}/${y}`;
}

export default function Postulaciones() {
    const { id } = useParams();
    const { token } = useAuth();
    const [datos, setDatos]         = useState(null);
    const [cargando, setCargando]   = useState(true);
    const [error, setError]         = useState('');

    useEffect(() => {
        getPostulaciones(id, token)
            .then(data => {
                if (data.error) setError(data.error);
                else setDatos(data);
            })
            .finally(() => setCargando(false));
    }, []);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Postulaciones recibidas</h4>

                {cargando && <LoadingSpinner />}
                {error && <div className="alert alert-danger">{error}</div>}

                {!cargando && datos && (
                    <>
                        <p className="text-muted">
                            Puesto: <strong>{datos.puestoNombre}</strong>
                        </p>

                        {datos.postulaciones.length === 0 && (
                            <div className="alert alert-info mt-3">
                                Aún no hay postulaciones para este puesto.
                            </div>
                        )}

                        {datos.postulaciones.length > 0 && (
                            <table className="table table-bordered table-hover mt-3">
                                <thead className="table-dark">
                                <tr>
                                    <th>Nombre</th>
                                    <th>Identificación</th>
                                    <th>Correo</th>
                                    <th>Teléfono</th>
                                    <th>Fecha postulación</th>
                                    <th>Estado</th>
                                    <th>CV</th>
                                </tr>
                                </thead>
                                <tbody>
                                {datos.postulaciones.map(p => (
                                    <tr key={p.id}>
                                        <td>{p.oferenteNombre}</td>
                                        <td>{p.identificacion}</td>
                                        <td>{p.oferenteCorreo}</td>
                                        <td>{p.oferenteTelefono}</td>
                                        <td>{formatFecha(p.fechaPostulacion)}</td>
                                        <td>
                                                <span className={`badge ${p.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 'bg-success'}`}>
                                                    {p.estado}
                                                </span>
                                        </td>
                                        <td>
                                            {p.rutaCurriculum ? (
                                                <a href={p.rutaCurriculum} target="_blank"
                                                   rel="noreferrer"
                                                   className="btn btn-sm btn-success">
                                                    Ver CV
                                                </a>
                                            ) : (
                                                <span className="text-muted small">Sin CV</span>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}

                        <Link to="/empresa/puestos" className="btn btn-outline-secondary mt-2">
                            Volver
                        </Link>
                    </>
                )}
            </main>

            <Footer />
        </div>
    );
}