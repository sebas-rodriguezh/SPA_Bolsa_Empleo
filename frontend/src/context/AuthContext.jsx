import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token,  setToken]  = useState(localStorage.getItem('token'));
    const [rol,    setRol]    = useState(localStorage.getItem('rol'));
    const [correo, setCorreo] = useState(localStorage.getItem('correo'));
    const [nombre, setNombre] = useState(localStorage.getItem('nombre'));
    const [userId, setUserId] = useState(localStorage.getItem('userId'));

    const login = (data) => {
        localStorage.setItem('token',  data.token);
        localStorage.setItem('rol',    data.rol);
        localStorage.setItem('correo', data.correo);
        localStorage.setItem('nombre', data.nombre);
        localStorage.setItem('userId', data.id);
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

    return (
        <AuthContext.Provider value={{ token, rol, correo, nombre, userId, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);