import { useState, useEffect } from 'react';
import { useParams, useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getDetalleCandidato } from '../../api/empresa';

export default function DetalleCandidato() {
    const { oferenteId }            = useParams();
    const [searchParams]            = useSearchParams();
    const puestoId                  = searchParams.get('puestoId');
    const { token }                 = useAuth();
    const [datos, setDatos]         = useState(null);
    const [cargando, setCargando]   = useState(true);
    const [error, setError]         = useState('');

    useEffect(() => {
        getDetalleCandidato(oferenteId, puestoId, token)
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
                <h4>Detalle de oferente</h4>

                {cargando && <LoadingSpinner />}
                {error && <div className="alert alert-danger">{error}</div>}

                {!cargando && datos && (
                    <>
                        <div className="border rounded p-3 bg-light mb-4"
                             style={{ maxWidth: '500px' }}>
                            <p className="fw-bold fs-5 mb-2">
                                {datos.nombre} {datos.primerApellido}
                            </p>
                            <p>
                                <span className="text-muted">Identificación: </span>
                                {datos.identificacion}
                            </p>
                            <p>
                                <span className="text-muted">Email: </span>
                                {datos.correo}
                            </p>
                            <p>
                                <span className="text-muted">Teléfono: </span>
                                {datos.telefono}
                            </p>
                            <p>
                                <span className="text-muted">Residencia: </span>
                                {datos.lugarResidencia}
                            </p>

                            {datos.rutaCurriculum ? (
                                <div className="mt-2">
                                    <a href={datos.rutaCurriculum} target="_blank"
                                       rel="noreferrer" className="btn btn-sm btn-success">
                                        Ver CV
                                    </a>
                                </div>
                            ) : (
                                <p className="text-muted small mt-2">Sin CV registrado.</p>
                            )}
                        </div>

                        <h5>Habilidades</h5>

                        {datos.habilidades.length === 0 && (
                            <p className="text-muted fst-italic">
                                Este oferente no tiene habilidades registradas.
                            </p>
                        )}

                        {datos.habilidades.length > 0 && (
                            <table className="table table-bordered table-sm mt-2"
                                   style={{ maxWidth: '500px' }}>
                                <thead className="table-secondary">
                                <tr>
                                    <th>Característica</th>
                                    <th>Nivel</th>
                                </tr>
                                </thead>
                                <tbody>
                                {datos.habilidades.map(h => (
                                    <tr key={h.id}>
                                        <td>{h.rutaCompleta}</td>
                                        <td>{h.nivel}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}

                        <Link
                            to={`/empresa/puestos/${puestoId}/candidatos`}
                            className="btn btn-outline-secondary mt-3"
                        >
                            Volver
                        </Link>
                    </>
                )}
            </main>

            <Footer />
        </div>
    );
}