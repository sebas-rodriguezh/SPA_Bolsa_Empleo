import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ rol }) {
    const { token, rol: userRol } = useAuth();

    if (!token) return <Navigate to="/login" replace />;
    if (rol && userRol !== rol) return <Navigate to="/" replace />;

    return <Outlet />;
}