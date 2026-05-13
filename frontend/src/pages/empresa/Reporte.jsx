import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getReporteEmpresa } from '../../api/empresa';

function formatFecha(fecha) {
    if (!fecha) return '';
    const [y, m, d] = fecha.split('-');
    return `${d}/${m}/${y}`;
}

function formatSalario(salario, moneda) {
    const simbolo = moneda === 'USD' ? '$' : moneda === 'EUR' ? '€' : '₡';
    return `${simbolo} ${Number(salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}`;
}

export default function Reporte() {
    const { token } = useAuth();
    const [datos, setDatos]               = useState(null);
    const [cargando, setCargando]         = useState(true);
    const [puestoSeleccionado, setPuestoSeleccionado] = useState('');
    const [buscando, setBuscando]         = useState(false);

    useEffect(() => {
        getReporteEmpresa(token)
            .then(data => setDatos(data))
            .finally(() => setCargando(false));
    }, []);

    const handleVerReporte = async (e) => {
        e.preventDefault();
        if (!puestoSeleccionado) return;
        setBuscando(true);
        try {
            const data = await getReporteEmpresa(token, puestoSeleccionado);
            setDatos(data);
        } finally {
            setBuscando(false);
        }
    };

    const handleLimpiar = async () => {
        setPuestoSeleccionado('');
        setBuscando(true);
        try {
            const data = await getReporteEmpresa(token);
            setDatos(data);
        } finally {
            setBuscando(false);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>

                <div className="d-flex justify-content-between align-items-center mb-3 no-print">
                    <h4 className="mb-0">Reporte de Postulaciones por Puesto</h4>
                    {datos?.postulaciones && (
                        <button className="btn-imprimir" onClick={() => window.print()}>
                            Imprimir / Guardar PDF
                        </button>
                    )}
                </div>

                {cargando && <LoadingSpinner />}

                {!cargando && datos && (
                    <>
                        {/* Formulario selector de puesto */}
                        <div className="border rounded p-3 bg-light mb-4 no-print">
                            <form onSubmit={handleVerReporte} className="row g-2 align-items-end">
                                <div className="col-auto">
                                    <label className="form-label fw-semibold mb-1">
                                        Seleccionar puesto
                                    </label>
                                    <select
                                        className="form-select form-select-sm"
                                        value={puestoSeleccionado}
                                        onChange={e => setPuestoSeleccionado(e.target.value)}
                                        required
                                    >
                                        <option value="" disabled>Seleccione un puesto</option>
                                        {datos.puestos.map(p => (
                                            <option key={p.id} value={p.id}>
                                                {p.nombre}{!p.activo ? ' (inactivo)' : ''}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div className="col-auto">
                                    <button type="submit" className="btn btn-primary btn-sm"
                                            disabled={buscando}>
                                        Ver reporte
                                    </button>
                                </div>
                                {datos.puesto && (
                                    <div className="col-auto">
                                        <button type="button"
                                                className="btn btn-outline-secondary btn-sm"
                                                onClick={handleLimpiar}>
                                            Limpiar
                                        </button>
                                    </div>
                                )}
                            </form>
                        </div>

                        {datos.puestos.length === 0 && (
                            <div className="alert alert-info no-print">
                                No tenés puestos publicados aún.
                            </div>
                        )}

                        {/* Reporte del puesto seleccionado */}
                        {datos.puesto && (
                            <>
                                <div className="border rounded p-3 bg-white mb-3"
                                     style={{ borderLeft: '4px solid #0066cc' }}>
                                    <p className="mb-1 fw-bold">{datos.puesto.nombre}</p>
                                    <p className="mb-1">
                                        <span className="text-muted">Salario: </span>
                                        {formatSalario(datos.puesto.salario, datos.puesto.moneda)}
                                    </p>
                                    <p className="mb-0 fw-bold">
                                        Total de postulantes: {datos.puesto.totalPostulantes}
                                    </p>
                                </div>

                                {datos.postulaciones.length === 0 && (
                                    <div className="alert alert-warning">
                                        No hay postulaciones registradas para este puesto aún.
                                    </div>
                                )}

                                {datos.postulaciones.length > 0 && (
                                    <table className="table table-bordered table-hover table-sm">
                                        <thead className="table-secondary">
                                        <tr>
                                            <th>#</th>
                                            <th>Nombre completo</th>
                                            <th>Identificación</th>
                                            <th>Correo</th>
                                            <th>Teléfono</th>
                                            <th>Residencia</th>
                                            <th>Fecha postulación</th>
                                            <th>Estado</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {datos.postulaciones.map((p, i) => (
                                            <tr key={i}>
                                                <td>{i + 1}</td>
                                                <td>{p.nombre}</td>
                                                <td>{p.identificacion}</td>
                                                <td>{p.correo}</td>
                                                <td>{p.telefono}</td>
                                                <td>{p.residencia}</td>
                                                <td>{formatFecha(p.fechaPostulacion)}</td>
                                                <td>
                                                        <span className={`badge ${p.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 'bg-success'}`}>
                                                            {p.estado}
                                                        </span>
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                )}
                            </>
                        )}
                    </>
                )}
            </main>

            <Footer />
        </div>
    );
}