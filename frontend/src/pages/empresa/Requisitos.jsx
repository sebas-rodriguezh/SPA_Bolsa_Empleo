import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getRequisitos, agregarRequisito, quitarRequisito } from '../../api/empresa';
import { getCaracteristicasPublico } from '../../api/publico';

export default function Requisitos() {
    const { id } = useParams();
    const { token } = useAuth();
    const navigate = useNavigate();

    const [arbol, setArbol]           = useState([]);
    const [requisitos, setRequisitos] = useState([]);
    const [cargando, setCargando]     = useState(true);
    const [actualId, setActualId]     = useState(null);
    const [nivel, setNivel]           = useState(1);
    const [error, setError]           = useState('');
    const [exito, setExito]           = useState('');
    const [enviando, setEnviando]     = useState(false);

    const cargar = () => {
        Promise.all([
            getCaracteristicasPublico(),
            getRequisitos(id, token)
        ]).then(([arbolData, reqData]) => {
            setArbol(Array.isArray(arbolData) ? arbolData : []);
            setRequisitos(Array.isArray(reqData) ? reqData : []);
        }).finally(() => setCargando(false));
    };

    useEffect(() => { cargar(); }, []);

    const actual = arbol.find(c => c.id === actualId) || null;

    const categorias = arbol.filter(c =>
        actualId === null
            ? c.padreId === '' || c.padreId === null || c.padreId === undefined
            : c.padreId === actualId
    );

    const buildRuta = (nodeId) => {
        const ruta = [];
        let cursor = arbol.find(c => c.id === nodeId);
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
            const data = await agregarRequisito(id, {
                caracteristicaId: actualId,
                nivel: Number(nivel)
            }, token);
            if (data.error) {
                setError(data.error);
            } else {
                setExito('Requisito agregado correctamente.');
                setNivel(1);
                cargar();
            }
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setEnviando(false);
        }
    };

    const handleQuitar = async (pcId) => {
        try {
            await quitarRequisito(id, pcId, token);
            setRequisitos(prev => prev.filter(r => r.id !== pcId));
        } catch {
            setError('Error al quitar el requisito.');
        }
    };

    const handleFinalizar = () => {
        if (requisitos.length === 0) {
            setError('Debe agregar al menos una característica antes de publicar el puesto.');
            return;
        }
        navigate('/empresa/puestos');
    };

    useEffect(() => {
        setError('');
        setExito('');
    }, [actualId]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <nav className="navbar navbar-expand-lg bg-dark navbar-dark">
                <div className="container-fluid">
                    <span className="navbar-brand">BolsaEmpleo</span>
                    <span className="text-light small fst-italic">
                        Complete los requisitos del puesto antes de continuar
                    </span>
                </div>
            </nav>

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Características requeridas</h4>

                {error && <div className="alert alert-danger mt-2">{error}</div>}
                {exito && <div className="text-success small fw-semibold mb-2">{exito}</div>}

                {cargando && <LoadingSpinner />}

                {!cargando && (
                    <div className="row mt-3 g-4">

                        {/* Panel izquierdo: requisitos agregados */}
                        <div className="col-md-4">
                            <div className="border rounded p-3 bg-light h-100">
                                <p className="fw-bold mb-3">Características requeridas</p>

                                {requisitos.length === 0 && (
                                    <p className="text-muted fst-italic small">
                                        Aún no hay características. Agregue al menos una.
                                    </p>
                                )}

                                {requisitos.length > 0 && (
                                    <table className="table table-sm table-bordered bg-white">
                                        <thead className="table-secondary">
                                        <tr>
                                            <th>Característica</th>
                                            <th>Nivel</th>
                                            <th></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {requisitos.map(r => (
                                            <tr key={r.id}>
                                                <td className="small">{r.rutaCompleta}</td>
                                                <td>{r.nivel}</td>
                                                <td>
                                                    <button
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={() => handleQuitar(r.id)}
                                                    >
                                                        ✕
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}

                                <button
                                    className="btn btn-success btn-sm w-100 mt-2"
                                    onClick={handleFinalizar}
                                >
                                    Finalizar
                                </button>
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

                        {/* Panel derecho: agregar requisito */}
                        <div className="col-md-4">
                            <div className="border rounded p-3 bg-light h-100">
                                <p className="fw-bold mb-3">Agregar Característica</p>

                                {actual === null && (
                                    <p className="text-muted small fst-italic">
                                        Navegue el árbol hasta la característica que desea requerir.
                                    </p>
                                )}

                                {actual !== null && (
                                    <>
                                        <p className="text-muted small mb-1">
                                            Va a agregar: <strong>{actual.nombre}</strong>
                                        </p>

                                        {categorias.length > 0 && (
                                            <div className="alert alert-info py-2 small mb-3">
                                                Tiene subcategorías. También puede entrar en ellas para ser más específico.
                                            </div>
                                        )}

                                        <form onSubmit={handleAgregar}>
                                            <div className="mb-3">
                                                <label className="form-label mb-1">Nivel requerido (1-5)</label>
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