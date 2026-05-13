import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login as loginApi } from '../api/auth';

export default function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [correo, setCorreo] = useState('');
    const [clave, setClave]   = useState('');
    const [error, setError]   = useState('');
    const [cargando, setCargando] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setCargando(true);
        try {
            const data = await loginApi(correo.trim(), clave);
            if (data.error) { setError(data.error); return; }
            login(data);
            if (data.rol === 'ADMIN')         navigate('/admin/dashboard');
            else if (data.rol === 'EMPRESA')  navigate('/empresa/dashboard');
            else if (data.rol === 'OFERENTE') navigate('/oferente/dashboard');
            else navigate('/');
        } catch {
            setError('Error de conexión con el servidor.');
        } finally {
            setCargando(false);
        }
    };

    return (
        <div style={{ fontFamily: 'Arial, sans-serif', maxWidth: '400px', margin: '40px auto', padding: '0 20px' }}>
            <h1 style={{ color: '#333', borderBottom: '2px solid #333', paddingBottom: '10px' }}>
                Bolsa de Empleo
            </h1>

            {error && (
                <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffeeee', borderRadius: '4px' }}>
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                        Correo electrónico:
                    </label>
                    <input type="email" value={correo} onChange={e => setCorreo(e.target.value)}
                           required style={{ width: '100%', padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }} />
                </div>
                <div style={{ marginBottom: '15px' }}>
                    <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
                        Contraseña:
                    </label>
                    <input type="password" value={clave} onChange={e => setClave(e.target.value)}
                           required minLength={8}
                           style={{ width: '100%', padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }} />
                </div>
                <button type="submit" disabled={cargando}
                        style={{ backgroundColor: '#0066cc', color: 'white', padding: '10px 20px', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '16px' }}>
                    {cargando ? 'Ingresando...' : 'Iniciar Sesión'}
                </button>
            </form>

            <div style={{ marginTop: '20px', textAlign: 'center' }}>
                <Link to="/" style={{ color: '#0066cc' }}>Volver al inicio</Link>
            </div>
        </div>
    );
}