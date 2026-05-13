import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registrarOferente } from '../api/auth';

const inputStyle = { width: '100%', padding: '8px', border: '1px solid #ddd', borderRadius: '4px' };

export default function RegistroOferente() {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        identificacion: '', nombre: '', primerApellido: '',
        nacionalidad: '', telefono: '', correo: '',
        clave: '', lugarResidencia: ''
    });
    const [error, setError]       = useState('');
    const [cargando, setCargando] = useState(false);

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setCargando(true);
        try {
            const data = await registrarOferente(form);
            if (data.error) { setError(data.error); return; }
            navigate('/registro-pendiente');
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setCargando(false);
        }
    };

    const campos = [
        { label: 'Identificación:', name: 'identificacion', type: 'text' },
        { label: 'Nombre:', name: 'nombre', type: 'text' },
        { label: 'Primer Apellido:', name: 'primerApellido', type: 'text' },
        { label: 'Nacionalidad:', name: 'nacionalidad', type: 'text' },
        { label: 'Teléfono:', name: 'telefono', type: 'text' },
        { label: 'Correo electrónico:', name: 'correo', type: 'email' },
        { label: 'Contraseña:', name: 'clave', type: 'password' },
        { label: 'Lugar de Residencia:', name: 'lugarResidencia', type: 'text' },
    ];

    return (
        <div style={{ fontFamily: 'Arial, sans-serif', maxWidth: '400px', margin: '40px auto', padding: '0 20px' }}>
            <h1 style={{ color: '#333', borderBottom: '2px solid #333', paddingBottom: '10px' }}>
                Registro de Oferente
            </h1>

            {error && (
                <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffeeee', borderRadius: '4px' }}>
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                {campos.map(({ label, name, type }) => (
                    <div key={name} style={{ marginBottom: '15px' }}>
                        <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>{label}</label>
                        <input type={type} name={name} value={form[name]}
                               onChange={handleChange} required style={inputStyle} />
                    </div>
                ))}
                <button type="submit" disabled={cargando}
                        style={{ backgroundColor: '#0066cc', color: 'white', padding: '10px 20px', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '16px' }}>
                    {cargando ? 'Registrando...' : 'Registrarse'}
                </button>
            </form>

            <div style={{ marginTop: '20px', textAlign: 'center' }}>
                <Link to="/login" style={{ color: '#0066cc' }}>¿Ya tenés cuenta? Iniciá sesión</Link>
            </div>
        </div>
    );
}