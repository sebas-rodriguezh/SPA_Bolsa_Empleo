import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getPuestosRecientes } from '../api/publico';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function formatSalario(salario, moneda) {
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${Number(salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}

function formatFecha(fecha) {
    if (!fecha) return '';
    const [y, m, d] = fecha.split('-');
    return `${d}/${m}/${y}`;
}

export default function Index() {
    const { token } = useAuth();
    const [puestos, setPuestos]   = useState([]);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        getPuestosRecientes()
            .then(data => setPuestos(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    }, []);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main style={{ flex: 1, maxWidth: '1200px', margin: '2rem auto', padding: '0 2rem', width: '100%' }}>
                <h1 style={{ fontSize: '2rem', color: '#333', marginBottom: '0.5rem' }}>
                    Encuentra tu próximo empleo
                </h1>
                <p style={{ color: '#666', marginBottom: '2rem', fontSize: '1.1rem' }}>
                    Descubre las últimas oportunidades laborales publicadas por nuestras empresas
                </p>

                <h2 style={{ marginTop: '2rem', color: '#333' }}>📌 Puestos recién registrados</h2>

                {cargando && <p className="text-muted mt-3">Cargando puestos...</p>}

                <div className="puestos-grid">
                    {!cargando && puestos.length === 0 && (
                        <div className="loading-placeholder">
                            <p>No hay puestos publicados aún. ¡Vuelve pronto!</p>
                        </div>
                    )}

                    {puestos.map(puesto => (
                        <div className="puesto-card" key={puesto.id}>
                            <h3 className="puesto-titulo">{puesto.nombre}</h3>
                            <div className="puesto-empresa">{puesto.empresaNombre}</div>
                            <p className="puesto-descripcion">{puesto.descripcion}</p>
                            <div className="puesto-detalles">
                                <span className="puesto-salario">
                                    {formatSalario(puesto.salario, puesto.moneda)}
                                </span>
                                <span className="puesto-fecha">
                                    {formatFecha(puesto.fechaRegistro)}
                                </span>
                            </div>
                            <div>
                                <span className={`puesto-tipo ${puesto.esPublico ? 'tipo-publico' : 'tipo-privado'}`}>
                                    {puesto.esPublico ? 'Público' : 'Privado'}
                                </span>
                            </div>
                            <div className="tooltip-caracteristicas">
                                <h4>{puesto.nombre}</h4>
                                {Array.isArray(puesto.requisitos) && puesto.requisitos.length > 0 ? (
                                    <ul>
                                        {puesto.requisitos.map((r, i) => (
                                            <li key={i}>
                                                {r.caracteristicaNombre ?? r.nombre ?? r.caracteristica}
                                                {r.nivel ? ` (nivel ${r.nivel})` : ''}
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p className="tooltip-sin-requisitos">Ver detalle</p>
                                )}
                            </div>
                        </div>
                    ))}
                </div>

                {!token && (
                    <div className="invite-msg">
                        <strong>¿Querés ver todos los puestos disponibles?</strong><br />
                        <Link to="/login">Iniciá sesión</Link> o <Link to="/registro">registrate</Link> para acceder a todas las oportunidades laborales.
                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}