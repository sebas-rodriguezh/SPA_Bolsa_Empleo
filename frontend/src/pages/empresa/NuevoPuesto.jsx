import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import { crearPuesto } from '../../api/empresa';

export default function NuevoPuesto() {
    const { token } = useAuth();
    const navigate  = useNavigate();

    const [form, setForm] = useState({
        nombre: '', descripcion: '', salario: '',
        esPublico: '', moneda: 'CRC'
    });
    const [error, setError]       = useState('');
    const [cargando, setCargando] = useState(false);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const salarioNum = parseFloat(form.salario);
        if (isNaN(salarioNum) || salarioNum <= 0) {
            setError('El salario debe ser un número mayor a 0.');
            return;
        }
        if (!form.esPublico) {
            setError('Debe seleccionar si el puesto es público o privado.');
            return;
        }

        setCargando(true);
        try {
            const data = await crearPuesto({
                nombre:      form.nombre.trim(),
                descripcion: form.descripcion.trim(),
                salario:     salarioNum,
                esPublico:   form.esPublico === 'true',
                moneda:      form.moneda
            }, token);

            if (data.error) {
                setError(data.error);
                return;
            }

            // Redirigir a requisitos con el id del puesto recién creado
            navigate(`/empresa/puestos/${data.id}/requisitos`);
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setCargando(false);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
            <Navbar />

            <main className="container mt-4" style={{ flex: 1 }}>
                <h4>Publicar nuevo puesto</h4>

                {error && (
                    <div className="alert alert-danger mt-3">{error}</div>
                )}

                <div className="row mt-3">
                    <div className="col-md-6">
                        <div className="border rounded p-3 bg-light">
                            <form onSubmit={handleSubmit}>

                                <div className="mb-3">
                                    <label className="form-label fw-semibold">
                                        Nombre del puesto <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        name="nombre"
                                        className="form-control"
                                        placeholder="Ej: Desarrollador Backend Java"
                                        value={form.nombre}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="mb-3">
                                    <label className="form-label fw-semibold">
                                        Descripción <span className="text-danger">*</span>
                                    </label>
                                    <textarea
                                        name="descripcion"
                                        className="form-control"
                                        rows="4"
                                        placeholder="Describa el puesto y sus responsabilidades..."
                                        value={form.descripcion}
                                        onChange={handleChange}
                                        required
                                        minLength={10}
                                    />
                                    <div className="form-text">Mínimo 10 caracteres.</div>
                                </div>

                                <div className="mb-3">
                                    <label className="form-label fw-semibold">
                                        Salario <span className="text-danger">*</span>
                                    </label>
                                    <input
                                        type="text"
                                        name="salario"
                                        className="form-control"
                                        placeholder="Ej: 1500000"
                                        value={form.salario}
                                        onChange={handleChange}
                                        required
                                    />
                                    <div className="form-text">Solo números, sin símbolos ni comas.</div>
                                </div>

                                <div className="mb-3">
                                    <label className="form-label fw-semibold">
                                        Moneda <span className="text-danger">*</span>
                                    </label>
                                    <select
                                        name="moneda"
                                        className="form-select"
                                        value={form.moneda}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="CRC">₡ Colón costarricense (CRC)</option>
                                        <option value="USD">$ Dólar estadounidense (USD)</option>
                                        <option value="EUR">€ Euro (EUR)</option>
                                    </select>
                                </div>

                                <div className="mb-4">
                                    <label className="form-label fw-semibold">
                                        Tipo de publicación <span className="text-danger">*</span>
                                    </label>
                                    <select
                                        name="esPublico"
                                        className="form-select"
                                        value={form.esPublico}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="" disabled>Seleccione</option>
                                        <option value="true">Público (visible para todos)</option>
                                        <option value="false">Privado (solo oferentes registrados)</option>
                                    </select>
                                </div>

                                <div className="d-flex gap-2">
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        disabled={cargando}
                                    >
                                        {cargando ? 'Guardando...' : 'Siguiente: agregar características'}
                                    </button>
                                    <Link to="/empresa/dashboard" className="btn btn-outline-secondary">
                                        Cancelar
                                    </Link>
                                </div>

                            </form>
                        </div>
                    </div>
                </div>
            </main>

            <Footer />
        </div>
    );
}