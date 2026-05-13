const BASE = import.meta.env.VITE_API_URL;

export async function login(correo, clave) {
    const res = await fetch(`${BASE}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ correo, clave })
    });
    return res.json();
}

export async function registrarEmpresa(datos) {
    const res = await fetch(`${BASE}/api/auth/registro/empresa`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    });
    return res.json();
}

export async function registrarOferente(datos) {
    const res = await fetch(`${BASE}/api/auth/registro/oferente`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(datos)
    });
    return res.json();
}