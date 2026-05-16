import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getCaracteristicasPublico, buscarPuestosPublicos } from '../api/publico';
import { buscarPuestosOferente } from '../api/oferente'; // ── NUEVO: Importamos el endpoint privado
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';

function formatSalario(salario, moneda) {
    const num = parseFloat(salario);
    if (isNaN(num)) return '—';
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${num.toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}


function PuestoCard({ puesto }) {
    const requisitos = Array.isArray(puesto.requisitos) ? puesto.requisitos : [];

    return (
        <div className="puesto-card" key={puesto.id} style={{ display: 'flex', flexDirection: 'column' }}>
            <div className="puesto-titulo">{puesto.nombre}</div>
            <div className="puesto-empresa">{puesto.empresaNombre}</div>

            <div className="puesto-detalles">
                <span className="puesto-salario">
                    {formatSalario(puesto.salario, puesto.moneda)}
                </span>
            </div>

            <div style={{ marginBottom: '0.8rem' }}>
                <span className={`puesto-tipo ${puesto.esPublico ? 'tipo-publico' : 'tipo-privado'}`}>
                    {puesto.esPublico ? 'Público' : 'Privado'}
                </span>
            </div>

            <div className="tooltip-caracteristicas">
                <h4>{puesto.nombre}</h4>
                {requisitos.length > 0 ? (
                    <ul>
                        {requisitos.map((r, i) => (
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
    );
}

export default function BuscarPuestos() {
    const { token, rol } = useAuth();
    const [arbol, setArbol]                 = useState([]);
    const [seleccionados, setSeleccionados] = useState([]);
    const [moneda, setMoneda]               = useState('');
    const [resultados, setResultados]       = useState(null);
    const [cargando, setCargando]           = useState(false);

    useEffect(() => {
        getCaracteristicasPublico()
            .then(data => setArbol(Array.isArray(data) ? data : []));
    }, []);

    const toggleCaracteristica = (id) => {
        setSeleccionados(prev =>
            prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
        );
    };

    const handleBuscar = async (e) => {
        e.preventDefault();
        if (seleccionados.length === 0) return;
        setCargando(true);
        try {
            let data;
            if (token && rol === 'OFERENTE') {
                // El oferente autenticado puede ver públicos y privados
                data = await buscarPuestosOferente(seleccionados, moneda, token);
            } else {
                // Usuarios anónimos o de otro tipo solo ven públicos
                data = await buscarPuestosPublicos(seleccionados, moneda);
            }

            setResultados(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error("Error al buscar puestos:", error);
            setResultados([]);
        } finally {
            setCargando(false);
        }
    };

    const handleLimpiar = () => {
        setSeleccionados([]);
        setMoneda('');
        setResultados(null);
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main style={{ flex: 1, maxWidth: '1100px', margin: '2rem auto', padding: '0 2rem', width: '100%' }}>
                <h1 style={{ fontSize: '2rem', color: '#333', marginBottom: '1.5rem' }}>
                    Buscar puestos por características
                </h1>

                <div style={{
                    background: 'white', borderRadius: '10px', padding: '1.5rem',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.08)', border: '1px solid #e9ecef',
                    marginBottom: '2rem'
                }}>
                    <h3 style={{ fontSize: '1rem', color: '#333', marginBottom: '1rem' }}>
                        Seleccione las características que desea buscar:
                    </h3>
                    <form onSubmit={handleBuscar}>
                        <div className="arbol">
                            {arbol.map(c => (
                                <div
                                    key={c.id}
                                    className="arbol-item"
                                    style={{ paddingLeft: `${(c.nivel ?? 0) * 16}px` }}
                                >
                                    <input
                                        type="checkbox"
                                        id={`c${c.id}`}
                                        checked={seleccionados.includes(c.id)}
                                        onChange={() => toggleCaracteristica(c.id)}
                                    />
                                    <label htmlFor={`c${c.id}`}>{c.nombre}</label>
                                </div>
                            ))}
                        </div>

                        <div style={{ marginTop: '1.2rem' }}>
                            <label style={{ fontSize: '0.9rem', color: '#444', fontWeight: 500 }}>
                                Filtrar por moneda:&nbsp;
                            </label>
                            <select
                                value={moneda}
                                onChange={e => setMoneda(e.target.value)}
                                style={{
                                    marginLeft: '0.5rem', padding: '0.3rem 0.8rem',
                                    border: '1px solid #ccc', borderRadius: '5px', fontSize: '0.9rem'
                                }}
                            >
                                <option value="">Todas las monedas</option>
                                <option value="CRC">₡ Colón (CRC)</option>
                                <option value="USD">$ Dólar (USD)</option>
                                <option value="EUR">€ Euro (EUR)</option>
                            </select>
                        </div>

                        <button
                            type="submit"
                            disabled={cargando || seleccionados.length === 0}
                            style={{
                                backgroundColor: '#0066cc', color: 'white',
                                padding: '0.5rem 1.5rem', border: 'none', borderRadius: '5px',
                                cursor: 'pointer', fontSize: '1rem', marginTop: '1rem', marginRight: '0.5rem'
                            }}
                        >
                            {cargando ? 'Buscando...' : 'Buscar'}
                        </button>
                        <button
                            type="button"
                            onClick={handleLimpiar}
                            style={{
                                backgroundColor: 'transparent', color: '#666',
                                padding: '0.5rem 1.5rem', border: '1px solid #ccc',
                                borderRadius: '5px', fontSize: '1rem', marginTop: '1rem', cursor: 'pointer'
                            }}
                        >
                            Limpiar
                        </button>
                    </form>
                </div>

                {resultados !== null && (
                    <>
                        <h2 style={{ fontSize: '1.2rem', color: '#333', marginBottom: '1rem' }}>
                            Resultados ({resultados.length})
                        </h2>

                        {resultados.length === 0 && (
                            <p style={{ color: '#999', fontStyle: 'italic' }}>
                                No se encontraron puestos que coincidan con las características seleccionadas.
                            </p>
                        )}

                        <div className="puestos-grid">
                            {resultados.map(p => (
                                <PuestoCard key={p.id} puesto={p} />
                            ))}
                        </div>
                    </>
                )}
            </main>

            <Footer />
        </div>
    );
}