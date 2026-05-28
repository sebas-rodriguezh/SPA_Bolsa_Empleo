import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getCaracteristicas, crearCaracteristica, eliminarCaracteristica } from '../../api/admin';

export default function Caracteristicas() {
    const { token } = useAuth();
    const [arbol, setArbol] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [actualId, setActualId] = useState(null);
    const [nombre, setNombre] = useState('');
    const [padreId, setPadreId] = useState('');
    const [error, setError] = useState('');
    const [exito, setExito] = useState('');
    const [enviando, setEnviando] = useState(false);

    const cargar = () => {
        setCargando(true);
        getCaracteristicas(token)
            .then(data => setArbol(Array.isArray(data) ? data : []))
            .finally(() => setCargando(false));
    };

    useEffect(() => { cargar(); }, []);

    const actual = arbol.find(c => c.id === actualId) || null;

    const categorias = arbol.filter(c =>
        actualId === null
            ? c.padreId === '' || c.padreId === null || c.padreId === undefined
            : c.padreId === actualId
    );

    const esTerminal = (id) => !arbol.some(c => c.padreId === id);

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

    const handleCrear = async (e) => {
        e.preventDefault();
        setError('');
        setExito('');
        setEnviando(true);
        const payload = {
            nombre: nombre.trim(),
            padreId: padreId !== '' ? Number(padreId) : null
        };
        try {
            const data = await crearCaracteristica(payload, token);
            if (data.error) {
                setError(data.error);
            } else {
                setExito('Característica creada correctamente.');
                setNombre('');
                cargar();
            }
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setEnviando(false);
        }
    };

    const handleEliminar = async (id) => {
        if (!window.confirm(`¿Eliminar característica? Sus subcategorías también serán eliminadas.`)) return;
        setError('');
        setExito('');
        try {
            const data = await eliminarCaracteristica(id, token);
            if (data.error) {
                setError(data.error);
            } else {
                if (actualId === id) setActualId(null);
                setExito(`Característica eliminada correctamente.`);
                cargar();
            }
        } catch {
            setError('Error de conexión con el servidor.');
        }
    };

    useEffect(() => {
        setPadreId(actualId !== null ? actualId : '');
        setError('');
        setExito('');
    }, [actualId]);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Características</h4>

                {error && <div className="text-danger small fw-semibold mb-2">{error}</div>}
                {exito && <div className="text-success small fw-semibold mb-2">{exito}</div>}

                {cargando && <LoadingSpinner />}

                {!cargando && (
                    <div className="row mt-3 g-4">

                        {/* Panel izquierdo: árbol navegable */}
                        <div className="col-md-6">
                            <div className="border rounded p-3 bg-light">

                                <p className="mb-1"><strong>Ruta:</strong></p>
                                <div className="d-flex flex-wrap align-items-center gap-1 mb-2">
                                    <button className="btn btn-sm btn-outline-secondary"
                                            onClick={() => setActualId(null)}>
                                        Raíces
                                    </button>
                                    {ruta.map((nodo, i) => (
                                        <span key={nodo.id} className="d-flex align-items-center gap-1">
                                            <span className="text-muted">/</span>
                                            {i < ruta.length - 1 ? (
                                                <button className="btn btn-sm btn-outline-secondary"
                                                        onClick={() => setActualId(nodo.id)}>
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
                                    <p className="text-muted fst-italic">No hay subcategorías aún.</p>
                                )}

                                {categorias.map(cat => (
                                    <div key={cat.id}
                                         className="d-flex align-items-center border rounded bg-white p-2 mb-2 gap-2">
                                        <span className="fw-semibold flex-grow-1">
                                            {cat.nombre}
                                            {esTerminal(cat.id) && (
                                                <span
                                                    className="ms-2 badge bg-success"
                                                    style={{ fontSize: '0.65rem', verticalAlign: 'middle' }}
                                                    title="Nodo terminal: puede ser seleccionado como requisito o habilidad"
                                                >
                                                    hoja
                                                </span>
                                            )}
                                        </span>
                                        <div className="d-flex gap-2" role="group">
                                            {!esTerminal(cat.id) && (
                                                <button
                                                    className="btn btn-outline-secondary"
                                                    onClick={() => setActualId(cat.id)}
                                                    title="Entrar en subcategorías"
                                                >
                                                    Entrar
                                                </button>
                                            )}
                                            <button
                                                className="btn btn-outline-danger"
                                                onClick={() => handleEliminar(cat.id)}
                                                title="Eliminar"
                                            >
                                                ✕
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>

                        <div className="col-md-6">
                            <div className="border rounded p-3 bg-light">
                                <p className="fw-bold mb-3">Agregar Característica</p>

                                <form onSubmit={handleCrear}>
                                    <div className="row g-2 align-items-end">
                                        <div className="col-sm-4">
                                            <label className="form-label mb-1">Nombre</label>
                                            <input type="text" className="form-control form-control-sm"
                                                   placeholder="Nueva característica"
                                                   value={nombre}
                                                   onChange={e => setNombre(e.target.value)}
                                                   required />
                                        </div>

                                        <div className="col-sm-5">
                                            <label className="form-label mb-1">Padre</label>
                                            <select className="form-select form-select-sm"
                                                    value={padreId}
                                                    onChange={e => setPadreId(e.target.value)}>
                                                {categorias.map(op => (
                                                    <option key={op.id} value={op.id}>{op.nombre}</option>
                                                ))}
                                                <option value="">(sin padre)</option>
                                            </select>
                                        </div>

                                        <div className="col-sm-3">
                                            <button type="submit"
                                                    className="btn btn-primary btn-sm w-100"
                                                    disabled={enviando}>
                                                {enviando ? '...' : 'Crear'}
                                            </button>
                                        </div>
                                    </div>
                                </form>

                                <hr className="my-3" />
                                <p className="text-muted small mb-0">
                                    <span className="badge bg-success me-1" style={{ fontSize: '0.65rem' }}>hoja</span>
                                    Los nodos marcados como <strong>hoja</strong> son terminales: empresas y oferentes
                                    solo pueden seleccionarlos como requisitos o habilidades.
                                    Los nodos con subcategorías solo sirven para organizar la jerarquía.
                                </p>
                            </div>
                        </div>

                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}
