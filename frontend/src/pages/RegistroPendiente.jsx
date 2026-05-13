import { Link } from 'react-router-dom';

export default function RegistroPendiente() {
    return (
        <div style={{ fontFamily: 'Arial, sans-serif', maxWidth: '500px', margin: '80px auto', padding: '0 20px', textAlign: 'center' }}>
            <div style={{ border: '1px solid #ddd', borderRadius: '8px', padding: '40px 50px', backgroundColor: '#f9f9f9' }}>
                <div style={{ fontSize: '3rem', marginBottom: '10px' }}>⏳</div>
                <h1 style={{ color: '#333', borderBottom: '2px solid #333', paddingBottom: '10px', fontSize: '1.5rem' }}>
                    Registro recibido
                </h1>
                <p style={{ color: '#555', fontSize: '1rem', lineHeight: '1.7', margin: '20px 0' }}>
                    Tu solicitud fue enviada correctamente.<br /><br />
                    <strong>Estás esperando autorización para poder ingresar.</strong><br /><br />
                    Un administrador revisará tu solicitud próximamente.
                </p>
                <Link to="/"
                      style={{ display: 'inline-block', marginTop: '10px', backgroundColor: '#0066cc', color: 'white', padding: '10px 24px', borderRadius: '4px', textDecoration: 'none', fontSize: '1rem' }}>
                    Regresar al inicio
                </Link>
            </div>
        </div>
    );
}