import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getCV, actualizarCV, eliminarCV } from '../../api/oferente';

export default function CV() {
    const { token } = useAuth();
    const [rutaCV, setRutaCV]       = useState('');
    const [inputCV, setInputCV]     = useState('');
    const [cargando, setCargando]   = useState(true);
    const [error, setError]         = useState('');
    const [exito, setExito]         = useState('');
    const [enviando, setEnviando]   = useState(false);

    useEffect(() => {
        getCV(token)
            .then(data => {
                setRutaCV(data.rutaCurriculum || '');
                setInputCV(data.rutaCurriculum || '');
            })
            .finally(() => setCargando(false));
    }, []);

    const getUrlPreview = (url) => {
        if (!url) return null;
        return url.replace('/view', '/preview').replace('/edit', '/preview');
    };

    const handleGuardar = async (e) => {
        e.preventDefault();
        setError('');
        setExito('');
        setEnviando(true);
        try {
            const data = await actualizarCV(inputCV.trim(), token);
            if (data.error) {
                setError(data.error);
            } else {
                setExito('CV actualizado correctamente.');
                setRutaCV(inputCV.trim());
            }
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setEnviando(false);
        }
    };

    const handleEliminar = async () => {
        setError('');
        setExito('');
        try {
            await eliminarCV(token);
            setRutaCV('');
            setInputCV('');
            setExito('CV eliminado correctamente.');
        } catch {
            setError('Error al eliminar el CV.');
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4 className="fw-bold">Mi CV</h4>

                {cargando && <LoadingSpinner />}

                {!cargando && (
                    <div className="row">
                        <div className="col-md-6">

                            {error && (
                                <div className="alert alert-danger" style={{ fontSize: '0.9rem' }}>
                                    {error}
                                </div>
                            )}
                            {exito && (
                                <div className="alert alert-success" style={{ fontSize: '0.9rem' }}>
                                    {exito}
                                </div>
                            )}

                            {/* Tiene CV registrado */}
                            {rutaCV && (
                                <>
                                    <p className="mb-3">Tu CV está registrado:</p>

                                    <div className="d-flex gap-2 mb-3">
                                        <a href={rutaCV} target="_blank" rel="noreferrer"
                                           className="btn btn-success">
                                            Ver mi CV
                                        </a>
                                        <button
                                            className="btn btn-danger"
                                            onClick={handleEliminar}
                                        >
                                            Eliminar CV
                                        </button>
                                    </div>

                                    {getUrlPreview(rutaCV) && (
                                        <div className="mb-4">
                                            <p className="fw-semibold">Vista previa:</p>
                                            <iframe
                                                src={getUrlPreview(rutaCV)}
                                                width="100%"
                                                height="500px"
                                                style={{ border: '1px solid #dee2e6', borderRadius: '8px' }}
                                                title="Vista previa CV"
                                            />
                                        </div>
                                    )}

                                    <h6 className="mt-3">Actualizar link:</h6>
                                    <form onSubmit={handleGuardar}>
                                        <div className="mb-3">
                                            <input
                                                type="text"
                                                className="form-control"
                                                value={inputCV}
                                                onChange={e => setInputCV(e.target.value)}
                                                placeholder="Link de Google Drive o OneDrive"
                                            />
                                        </div>
                                        <button type="submit" className="btn btn-primary"
                                                disabled={enviando}>
                                            {enviando ? 'Actualizando...' : 'Actualizar'}
                                        </button>
                                    </form>
                                </>
                            )}

                            {/* No tiene CV */}
                            {!rutaCV && (
                                <>
                                    <p className="text-muted">No tenés ningún CV registrado aún.</p>
                                    <form onSubmit={handleGuardar}>
                                        <div className="mb-3">
                                            <label className="form-label">Link de tu CV:</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                value={inputCV}
                                                onChange={e => setInputCV(e.target.value)}
                                                placeholder="Link de Google Drive o OneDrive"
                                                required
                                            />
                                        </div>
                                        <button type="submit" className="btn btn-primary"
                                                disabled={enviando}>
                                            {enviando ? 'Guardando...' : 'Guardar'}
                                        </button>
                                    </form>
                                </>
                            )}

                        </div>
                    </div>
                )}
            </main>

            <Footer />
        </div>
    );
}