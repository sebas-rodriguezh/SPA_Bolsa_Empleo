import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import Footer from '../../components/Footer';
import { getMisPuestos, editarPuesto } from '../../api/empresa';

export default function EditarPuesto() {
    const { id } = useParams();
    const { token } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState(null);
    const [error, setError] = useState('');
    const [cargando, setCargando] = useState(true);
    const [guardando, setGuardando] = useState(false);

    // Cargar datos del puesto para pre-rellenar el formulario
    useEffect(() => {
        getMisPuestos(token).then(puestos => {
            const p = puestos.find(x => x.id === Number(id));
            if (p) setForm({
                nombre: p.nombre, descripcion: p.descripcion,
                salario: p.salario, esPublico: String(p.esPublico),
                moneda: p.moneda
            });
        }).finally(() => setCargando(false));
    }, []);

    const handleChange = (e) =>
        setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setGuardando(true);
        const data = await editarPuesto(id, {
            ...form,
            salario: parseFloat(form.salario),
            esPublico: form.esPublico === 'true'
        }, token);
        setGuardando(false);
        if (data.error) { setError(data.error); return; }
        navigate('/empresa/puestos');
    };

    if (cargando) return <p>Cargando...</p>;
    if (!form) return <p>Puesto no encontrado.</p>;

    return (
        <div style={{ display:'flex', flexDirection:'column', minHeight:'100vh' }}>
            <Navbar />
            <main className="container mt-4" style={{ flex:1 }}>
                <h4>Editar puesto</h4>
                {error && <div className="alert alert-danger">{error}</div>}
                <div className="col-md-6 mt-3">
                    <div className="border rounded p-3 bg-light">
                        <form onSubmit={handleSubmit}>
                            <div className="mb-3">
                                <label className="form-label">Nombre</label>
                                <input name="nombre" className="form-control"
                                       value={form.nombre} onChange={handleChange} required />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Descripción</label>
                                <textarea name="descripcion" className="form-control" rows="3"
                                          value={form.descripcion} onChange={handleChange} required />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Salario</label>
                                <input name="salario" className="form-control" type="number"
                                       value={form.salario} onChange={handleChange} required />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">Moneda</label>
                                <select name="moneda" className="form-select"
                                        value={form.moneda} onChange={handleChange}>
                                    <option value="CRC">₡ Colón (CRC)</option>
                                    <option value="USD">$ Dólar (USD)</option>
                                    <option value="EUR">€ Euro (EUR)</option>
                                </select>
                            </div>
                            <div className="mb-4">
                                <label className="form-label">Tipo</label>
                                <select name="esPublico" className="form-select"
                                        value={form.esPublico} onChange={handleChange}>
                                    <option value="true">Público</option>
                                    <option value="false">Privado</option>
                                </select>
                            </div>
                            <button type="submit" className="btn btn-primary"
                                    disabled={guardando}>
                                {guardando ? 'Guardando...' : 'Guardar cambios'}
                            </button>
                        </form>
                    </div>
                </div>
            </main>
            <Footer />
        </div>
    );
}