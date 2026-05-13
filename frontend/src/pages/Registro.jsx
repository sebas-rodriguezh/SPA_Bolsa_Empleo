import { Link } from 'react-router-dom';

export default function Registro() {
    return (
        <div style={{ fontFamily: 'Arial, sans-serif', maxWidth: '400px', margin: '40px auto', padding: '0 20px' }}>
            <h1 style={{ color: '#333', borderBottom: '2px solid #333', paddingBottom: '10px' }}>
                ¿Cómo desea registrarse?
            </h1>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginTop: '30px' }}>
                <Link to="/registro/empresa"
                      style={{ backgroundColor: '#0066cc', color: 'white', padding: '15px', textAlign: 'center', textDecoration: 'none', borderRadius: '4px', fontSize: '16px' }}>
                    Soy una Empresa
                </Link>
                <Link to="/registro/oferente"
                      style={{ backgroundColor: '#0066cc', color: 'white', padding: '15px', textAlign: 'center', textDecoration: 'none', borderRadius: '4px', fontSize: '16px' }}>
                    Soy un Oferente
                </Link>
            </div>
            <div style={{ marginTop: '20px', textAlign: 'center' }}>
                <Link to="/login" style={{ color: '#0066cc' }}>¿Ya tenés cuenta? Iniciá sesión</Link>
            </div>
        </div>
    );
}