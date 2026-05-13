import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getHabilidades, agregarHabilidad, eliminarHabilidad } from '../../api/oferente';
import { getCaracteristicasPublico } from '../../api/publico';

export default function Habilidades() {
    const { token } = useAuth();

    const [arbol, setArbol]           = useState([]);
    const [habilidades, setHabilidades] = useState([]);
    const [cargando, setCargando]     = useState(true);
    const [actualId, setActualId]     = useState(null);
    const [nivel, setNivel]           = useState(1);
    const [error, setError]           = useState('');
    const [exito, setExito]           = useState('');
    const [enviando, setEnviando]     = useState(false);

    const cargar = () => {
        Promise.all([
            getCaracteristicasPublico(),
            getHabilidades(token)
        ]).then(([arbolData, habilData]) => {
            setArbol(Array.isArray(arbolData) ? arbolData : []);
            setHabilidades(Array.isArray(habilData) ? habilData : []);
        }).finally(() => setCargando(false));
    };

    useEffect(() => { cargar(); }, []);

    const actual = arbol.find(c => c.id === actualId) || null;

    const categorias = arbol.filter(c =>
        actualId === null
            ? c.padreId === '' || c.padreId === null || c.padreId === undefined
            : c.padreId === actualId
    );

    const buildRuta = (id) => {
        const ruta = [];
        let cursor = arbol.find(c => c.id === id);
        while (cursor) {
            ruta.unshift(cursor);
            cursor = cursor.padreId ? arbol.find(c => c.id === cursor.padreId) : null;
        }
        return ruta;
    };
    const ruta = actual ? buildRuta(actual.id) : [];

    const handleAgregar = async (e) => {
        e.preventDefault();
        setError('');
        setExito('');
        setEnviando(true);
        try {
            const data = await agregarHabilidad(
                { caracteristicaId: actualId, nivel: Number(nivel) },
                token
            );
            if (data.error) {
                setError(data.error);
            } else {
                setExito('Habilidad registrada correctamente.');
                setNivel(1);
                cargar();
            }
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setEnviando(false);
        }
    };

    const handleEliminar = async (id) => {
        try {
            await eliminarHabilidad(id, token);
            setHabilidades(prev => prev.filter(h => h.id !== id));
        } catch {
            setError('Error al eliminar la habilidad.');
        }
    };

    useEffect(() => {
        setError('');
        setExito('');
    }, [actualId]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Mis habilidades</h4>

                {error && <div className="text-danger small fw-semibold mb-2">{error}</div>}
                {exito && <div className="text-success small fw-semibold mb-2">{exito}</div>}

                {cargando && <LoadingSpinner />}

                {!cargando && (
                    <div className="row mt-3 g-4">

                        {/* Panel izquierdo: habilidades registradas */}
                        <div className="col-md-4">
                            <div className="border rounded p-3 bg-light h-100">
                                <p className="fw-bold">Habilidades registradas</p>

                                {habilidades.length === 0 && (
                                    <p className="text-muted fst-italic">
                                        Aún no tenés habilidades registradas.
                                    </p>
                                )}

                                {habilidades.length > 0 && (
                                    <table className="table table-bordered table-hover table-sm">
                                        <thead className="table-secondary">
                                        <tr>
                                            <th>Característica</th>
                                            <th>Nivel</th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {habilidades.map(h => (
                                            <tr key={h.id}>
                                                <td className="small">{h.rutaCompleta}</td>
                                                <td>{h.nivel}</td>
                                                <td>
                                                    <button
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={() => handleEliminar(h.id)}
                                                    >
                                                        ✕
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}
                            </div>
                        </div>

                        {/* Panel central: árbol navegable */}
                        <div className="col-md-4">
                            <div className="border rounded p-3 bg-light h-100">
                                <p className="mb-1"><strong>Ruta:</strong></p>
                                <div className="d-flex flex-wrap align-items-center gap-1 mb-2">
                                    <button
                                        className="btn btn-sm btn-outline-secondary"
                                        onClick={() => setActualId(null)}
                                    >
                                        Raíces
                                    </button>
                                    {ruta.map((nodo, i) => (
                                        <span key={nodo.id} className="d-flex align-items-center gap-1">
                                            <span className="text-muted">/</span>
                                            {i < ruta.length - 1 ? (
                                                <button
                                                    className="btn btn-sm btn-outline-secondary"
                                                    onClick={() => setActualId(nodo.id)}
                                                >
                                                    {nodo.nombre}
                                                </button>
                                            ) : (
                                                <span className="btn btn-sm btn-outline-secondary disabled">
                                                    {nodo.nombre}
                                                </span>
                                            )}
                                        </span>
                                    ))}
                                </div>

                                <p className="text-muted small mb-3">
                                    {actual === null
                                        ? <>Categorías: <strong>raíces</strong></>
                                        : <>Subcategorías de: <strong>{actual.nombre}</strong></>
                                    }
                                </p>

                                {categorias.length === 0 && (
                                    <p className="text-muted fst-italic small">
                                        No hay subcategorías. Puede agregar esta característica desde el panel derecho.
                                    </p>
                                )}

                                {categorias.map(cat => (
                                    <div key={cat.id}
                                         className="d-flex justify-content-between align-items-center border rounded bg-white p-2 mb-2">
                                        <span className="fw-semibold small">{cat.nombre}</span>
                                        <button
                                            className="btn btn-sm btn-outline-secondary"
                                            onClick={() => setActualId(cat.id)}
                                        >
                                            Entrar
                                        </button>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Panel derecho: agregar habilidad */}
                        <div className="col-md-4">
                            <div className="border rounded p-3 bg-light h-100">
                                <p className="fw-bold">Agregar Habilidad</p>

                                {actual === null && (
                                    <p className="text-muted small fst-italic">
                                        Navegue el árbol hasta la característica que desea agregar.
                                    </p>
                                )}

                                {actual !== null && (
                                    <>
                                        <p className="text-muted small mb-1">
                                            Va a agregar: <strong>{actual.nombre}</strong>
                                        </p>

                                        {categorias.length > 0 && (
                                            <div className="alert alert-info py-2 small mb-3">
                                                Esta característica tiene subcategorías. También puede entrar en ellas para ser más específico.
                                            </div>
                                        )}

                                        <form onSubmit={handleAgregar}>
                                            <div className="mb-3">
                                                <label className="form-label">Nivel (1-5)</label>
                                                <input
                                                    type="number"
                                                    className="form-control form-control-sm"
                                                    min="1" max="5"
                                                    value={nivel}
                                                    onChange={e => setNivel(e.target.value)}
                                                    required
                                                />
                                                <div className="form-text">1 = básico · 5 = experto</div>
                                            </div>
                                            <button
                                                type="submit"
                                                className="btn btn-primary btn-sm w-100"
                                                disabled={enviando}
                                            >
                                                {enviando ? '...' : 'Agregar'}
                                            </button>
                                        </form>
                                    </>
                                )}
                            </div>
                        </div>

                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}