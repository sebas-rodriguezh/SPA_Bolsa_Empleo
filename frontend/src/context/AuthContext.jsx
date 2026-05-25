import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { useNavigate } from "react-router-dom";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {

    const navigate = useNavigate();
    const [token,  setToken]  = useState(localStorage.getItem('token'));
    const [rol,    setRol]    = useState(localStorage.getItem('rol'));
    const [correo, setCorreo] = useState(localStorage.getItem('correo'));
    const [nombre, setNombre] = useState(localStorage.getItem('nombre'));
    const [userId, setUserId] = useState(localStorage.getItem('userId'));
    const [sesionExpirada, setSesionExpirada] = useState(false);

    const login = (data) => {
        localStorage.setItem('token',  data.token);
        localStorage.setItem('rol',    data.rol);
        localStorage.setItem('correo', data.correo);
        localStorage.setItem('nombre', data.nombre);
        localStorage.setItem('userId', data.id);
        setSesionExpirada(false);
        setToken(data.token);
        setRol(data.rol);
        setCorreo(data.correo);
        setNombre(data.nombre);
        setUserId(data.id);
    };

    const logout = () => {
        localStorage.clear();
        setToken(null);
        setRol(null);
        setCorreo(null);
        setNombre(null);
        setUserId(null);
    };

    const expirarSesion = useCallback(() => {
        localStorage.clear();
        setToken(null);
        setRol(null);
        setSesionExpirada(true);
        navigate('/login');
    }, [navigate]);

    useEffect(() => {
        window.addEventListener('sesion-expirada', expirarSesion);
        return () => window.removeEventListener('sesion-expirada', expirarSesion);
    }, [expirarSesion]);

    return (
        <AuthContext.Provider value={{ token, rol, correo, nombre, userId, login, logout, sesionExpirada, expirarSesion }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);