import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getCandidatos } from '../../api/empresa';

export default function Candidatos() {
    const { id } = useParams();
    const { token } = useAuth();

    const [candidatos, setCandidatos] = useState([]);
    const [cargando, setCargando]     = useState(true);
    const [modo, setModo]             = useState('parcial');

    const cargar = (modoActual) => {
        setCargando(true);
        getCandidatos(id, modoActual, token)
            .then(data => setCandidatos(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    };

    useEffect(() => { cargar(modo); }, [modo]);

    const handleModo = (nuevoModo) => {
        setModo(nuevoModo);
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Candidatos para el puesto</h4>

                <div className="mb-3 d-flex gap-2">
                    <button
                        className={`btn btn-sm ${modo === 'parcial' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => handleModo('parcial')}
                    >
                        Al menos un requisito
                    </button>
                    <button
                        className={`btn btn-sm ${modo === 'completo' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => handleModo('completo')}
                    >
                        Todos los requisitos
                    </button>
                </div>

                {cargando && <LoadingSpinner />}

                {!cargando && candidatos.length === 0 && (
                    <div className="alert alert-info mt-3">
                        No se encontraron candidatos cuyas habilidades coincidan con los requisitos de este puesto.
                    </div>
                )}

                {!cargando && candidatos.length > 0 && (
                    <table className="table table-bordered table-hover mt-3">
                        <thead className="table-dark">
                        <tr>
                            <th>Oferente</th>
                            <th>Requisitos cumplidos</th>
                            <th>% Coincidencia</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        {candidatos.map(c => (
                            <tr key={c.oferenteId}>
                                <td>{c.nombre} {c.primerApellido}</td>
                                <td>{c.cumplidos} / {c.total}</td>
                                <td>{Number(c.porcentaje).toFixed(2)}%</td>
                                <td>
                                    <Link
                                        to={`/empresa/candidatos/${c.oferenteId}?puestoId=${id}`}
                                        className="btn btn-sm btn-primary"
                                    >
                                        Ver detalle
                                    </Link>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}

                <Link to="/empresa/puestos" className="btn btn-outline-secondary mt-2">
                    Volver
                </Link>
            </main>

            <Footer />
        </div>
    );
}