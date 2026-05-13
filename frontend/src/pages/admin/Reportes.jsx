import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import LoadingSpinner from '../../components/LoadingSpinner';
import { getReportes } from '../../api/admin';

const MESES = [
    { v: 1, n: 'Enero' }, { v: 2, n: 'Febrero' }, { v: 3, n: 'Marzo' },
    { v: 4, n: 'Abril' }, { v: 5, n: 'Mayo' }, { v: 6, n: 'Junio' },
    { v: 7, n: 'Julio' }, { v: 8, n: 'Agosto' }, { v: 9, n: 'Septiembre' },
    { v: 10, n: 'Octubre' }, { v: 11, n: 'Noviembre' }, { v: 12, n: 'Diciembre' },
];

function TablaReporte({ puestos }) {
    return (
        <table className="table table-sm table-bordered table-hover mb-0">
            <thead className="table-secondary">
            <tr>
                <th style={{ width: '4%' }}>#</th>
                <th style={{ width: '18%' }}>Nombre del puesto</th>
                <th style={{ width: '16%' }}>Empresa</th>
                <th style={{ width: '12%' }}>Salario</th>
                <th style={{ width: '8%' }}>Moneda</th>
                <th style={{ width: '10%' }}>Fecha registro</th>
                <th style={{ width: '8%' }}>Tipo</th>
                <th style={{ width: '8%' }}>Postulaciones</th>
            </tr>
            </thead>
            <tbody>
            {puestos.map((p, i) => (
                <tr key={p.id}>
                    <td>{i + 1}</td>
                    <td>{p.nombre}</td>
                    <td>{p.empresaNombre}</td>
                    <td>{Number(p.salario).toLocaleString('es-CR', { minimumFractionDigits: 2 })}</td>
                    <td>{p.moneda}</td>
                    <td>{p.fechaRegistro ? (() => { const [y,m,d] = p.fechaRegistro.split('-'); return `${d}/${m}/${y}`; })() : ''}</td>
                    <td>
                            <span className={`badge ${p.esPublico ? 'bg-success' : 'bg-warning text-dark'}`}>
                                {p.esPublico ? 'Público' : 'Privado'}
                            </span>
                    </td>
                    <td>{p.postulaciones ?? 0}</td>
                </tr>
            ))}
            </tbody>
        </table>
    );
}

export default function Reportes() {
    const { token } = useAuth();
    const [reporteCompleto, setReporteCompleto] = useState(null);
    const [reporteFiltrado, setReporteFiltrado] = useState(null);
    const [cargando, setCargando]   = useState(true);
    const [mesFiltro, setMesFiltro] = useState(new Date().getMonth() + 1);
    const [anioFiltro, setAnioFiltro] = useState(new Date().getFullYear());

    useEffect(() => {
        getReportes(token)
            .then(data => setReporteCompleto(data))
            .finally(() => setCargando(false));
    }, []);

    const handleFiltrar = async (e) => {
        e.preventDefault();
        setCargando(true);
        setReporteFiltrado(null);
        try {
            const data = await getReportes(token, mesFiltro, anioFiltro);
            setReporteFiltrado(data);
        } finally {
            setCargando(false);
        }
    };

    const handleVerTodos = async () => {
        setCargando(true);
        setReporteFiltrado(null);
        try {
            const data = await getReportes(token);
            setReporteCompleto(data);
        } finally {
            setCargando(false);
        }
    };

    const nombreMesFiltro = MESES.find(m => m.v === Number(mesFiltro))?.n || '';
    const totalPuestos = reporteCompleto
        ? Object.values(reporteCompleto).reduce((acc, lista) => acc + lista.length, 0)
        : 0;

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>

                <div className="d-flex justify-content-between align-items-center mb-3 no-print">
                    <h4 className="mb-0">Reporte de Puestos por Mes</h4>
                    <button className="btn-imprimir" onClick={() => window.print()}>
                        Imprimir / Guardar PDF
                    </button>
                </div>

                <div className="border rounded p-3 bg-light mb-4 no-print">
                    <form onSubmit={handleFiltrar} className="row g-2 align-items-end">
                        <div className="col-auto">
                            <label className="form-label fw-semibold mb-1">Mes</label>
                            <select className="form-select form-select-sm"
                                    value={mesFiltro}
                                    onChange={e => setMesFiltro(Number(e.target.value))}>
                                {MESES.map(m => (
                                    <option key={m.v} value={m.v}>{m.n}</option>
                                ))}
                            </select>
                        </div>
                        <div className="col-auto">
                            <label className="form-label fw-semibold mb-1">Año</label>
                            <input type="number" className="form-control form-control-sm"
                                   value={anioFiltro}
                                   onChange={e => setAnioFiltro(Number(e.target.value))}
                                   min="2026" max="2030" />
                        </div>
                        <div className="col-auto">
                            <button type="submit" className="btn btn-primary btn-sm">Filtrar</button>
                        </div>
                        <div className="col-auto">
                            <button type="button" className="btn btn-outline-secondary btn-sm"
                                    onClick={handleVerTodos}>
                                Ver todos
                            </button>
                        </div>
                    </form>
                </div>

                {cargando && <LoadingSpinner />}

                {/* Reporte filtrado por mes */}
                {!cargando && reporteFiltrado !== null && (
                    <>
                        {reporteFiltrado.error && (
                            <div className="alert alert-warning">{reporteFiltrado.error}</div>
                        )}
                        {!reporteFiltrado.error && reporteFiltrado.puestos?.length === 0 && (
                            <div className="alert alert-warning">No hay puestos registrados en ese mes.</div>
                        )}
                        {!reporteFiltrado.error && reporteFiltrado.puestos?.length > 0 && (
                            <div className="reporte-card">
                                <div className="mes-titulo">
                                    {nombreMesFiltro} {anioFiltro}
                                    <span className="total-badge">{reporteFiltrado.total} puesto(s)</span>
                                </div>
                                <TablaReporte puestos={reporteFiltrado.puestos} />
                            </div>
                        )}
                    </>
                )}

                {/* Reporte completo todos los meses */}
                {!cargando && reporteFiltrado === null && reporteCompleto !== null && (
                    <>
                        {Object.keys(reporteCompleto).length === 0 && (
                            <div className="alert alert-info">No hay puestos registrados en el sistema aún.</div>
                        )}
                        {Object.keys(reporteCompleto).length > 0 && (
                            <>
                                <div className="resumen-total">
                                    <strong>Total de meses con actividad:</strong> {Object.keys(reporteCompleto).length} mes(es)
                                    &nbsp;|&nbsp;
                                    <strong>Total de puestos registrados:</strong> {totalPuestos}
                                </div>
                                {Object.entries(reporteCompleto).map(([mes, puestos]) => (
                                    <div key={mes} className="reporte-card">
                                        <div className="mes-titulo">
                                            {mes}
                                            <span className="total-badge">{puestos.length} puesto(s)</span>
                                        </div>
                                        <TablaReporte puestos={puestos} />
                                    </div>
                                ))}
                            </>
                        )}
                    </>
                )}
            </main>

            <Footer />
        </div>
    );
}