const BASE = import.meta.env.VITE_API_URL;

function headers(token) {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

export async function apiFetch(url, options = {}) {
    const res = await fetch(url, options);

    // Si el backend responde con 401, el token expiró
    if (res.status === 401) {
        console.warn("Sesión expirada. Redirigiendo al login...");

        //Limpia el token del almacenamiento para que la app sepa que no está logueado
        localStorage.removeItem('token');

        window.location.href = '/login';
        return { error: "Sesión expirada" };
    }

    return res;
}