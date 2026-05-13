export default function LoadingSpinner({ texto = 'Cargando...' }) {
    return (
        <div className="d-flex justify-content-center align-items-center py-5">
            <div className="spinner-border text-primary me-2" role="status">
                <span className="visually-hidden">Cargando...</span>
            </div>
            <span className="text-muted">{texto}</span>
        </div>
    );
}