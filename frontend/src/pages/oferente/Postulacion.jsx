import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getPuestosDisponibles, postular } from '../../api/oferente';

function formatSalario(salario, moneda) {
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${Number(salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}

function formatFecha(fecha) {
    if (!fecha) return '';
    const [y, m, d] = fecha.split('-');
    return `${d}/${m}/${y}`;
}

export default function Postulacion() {
    const { token } = useAuth();
    const [puestos, setPuestos]     = useState([]);
    const [cargando, setCargando]   = useState(true);
    const [postulando, setPostulando] = useState(null);
    const [mensaje, setMensaje]     = useState('');

    const cargar = () => {
        getPuestosDisponibles(token)
            .then(data => setPuestos(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    };

    useEffect(() => { cargar(); }, []);

    const handlePostular = async (puestoId) => {
        setPostulando(puestoId);
        setMensaje('');
        try {
            const data = await postular(puestoId, token);
            if (data.error) {
                setMensaje(data.error);
            } else {
                // Marcar el puesto como ya postulado
                setPuestos(prev => prev.map(p =>
                    p.id === puestoId ? { ...p, yaPostulado: true } : p
                ));
                setMensaje('¡Postulación registrada correctamente!');
            }
        } catch {
            setMensaje('Error de conexión con el servidor.');
        } finally {
            setPostulando(null);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main style={{ flex: 1, maxWidth: '1200px', margin: '2rem auto', padding: '0 2rem', width: '100%' }}>
                <h1 style={{ fontSize: '2rem', color: '#333', marginBottom: '0.5rem' }}>
                    Postularme a un puesto
                </h1>
                <p style={{ color: '#666', marginBottom: '2rem', fontSize: '1.1rem' }}>
                    Seleccioná el puesto al que deseas aplicar
                </p>

                {mensaje && (
                    <div className={`alert ${mensaje.includes('correctamente') ? 'alert-success' : 'alert-danger'} mb-3`}>
                        {mensaje}
                    </div>
                )}

                {cargando && <LoadingSpinner />}

                <h2 style={{ marginTop: '2rem', color: '#333' }}>📌 Puestos disponibles</h2>

                <div className="puestos-grid">
                    {!cargando && puestos.length === 0 && (
                        <div className="loading-placeholder">
                            <p>No hay puestos disponibles para postular en este momento,
                                o ya te postulaste a todos los activos.</p>
                        </div>
                    )}

                    {puestos.map(puesto => (
                        <div className="puesto-card" key={puesto.id}
                             style={{ display: 'flex', flexDirection: 'column' }}>

                            <div className="puesto-titulo">{puesto.nombre}</div>
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

                            <div style={{ marginBottom: '0.8rem' }}>
                                <span className={`puesto-tipo ${puesto.esPublico ? 'tipo-publico' : 'tipo-privado'}`}>
                                    {puesto.esPublico ? 'Público' : 'Privado'}
                                </span>
                            </div>

                            {puesto.yaPostulado ? (
                                <div style={{ textAlign: 'center', fontSize: '0.85rem', color: '#28a745', padding: '0.4rem', marginTop: 'auto' }}>
                                    ✓ Ya te postulaste
                                </div>
                            ) : (
                                <button
                                    onClick={() => handlePostular(puesto.id)}
                                    disabled={postulando === puesto.id}
                                    style={{
                                        display: 'block', width: '100%', padding: '0.5rem 1rem',
                                        backgroundColor: '#0066cc', color: 'white', border: 'none',
                                        borderRadius: '5px', fontSize: '0.9rem', cursor: 'pointer',
                                        marginTop: 'auto'
                                    }}
                                >
                                    {postulando === puesto.id ? 'Postulando...' : 'Postularme'}
                                </button>
                            )}
                        </div>
                    ))}
                </div>
            </main>

            <Footer />
        </div>
    );
}