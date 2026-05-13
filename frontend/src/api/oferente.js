const BASE = import.meta.env.VITE_API_URL;

function headers(token) {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

export async function getOferenteDashboard(token) {
    const res = await fetch(`${BASE}/api/oferente/dashboard`, { headers: headers(token) });
    return res.json();
}

export async function getHabilidades(token) {
    const res = await fetch(`${BASE}/api/oferente/habilidades`, { headers: headers(token) });
    return res.json();
}

export async function agregarHabilidad(datos, token) {
    const res = await fetch(`${BASE}/api/oferente/habilidades`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify(datos)
    });
    return res.json();
}

export async function eliminarHabilidad(id, token) {
    const res = await fetch(`${BASE}/api/oferente/habilidades/${id}`, {
        method: 'DELETE',
        headers: headers(token)
    });
    return res.json();
}

export async function getCV(token) {
    const res = await fetch(`${BASE}/api/oferente/cv`, { headers: headers(token) });
    return res.json();
}

export async function actualizarCV(rutaCurriculum, token) {
    const res = await fetch(`${BASE}/api/oferente/cv`, {
        method: 'PUT',
        headers: headers(token),
        body: JSON.stringify({ rutaCurriculum })
    });
    return res.json();
}

export async function eliminarCV(token) {
    const res = await fetch(`${BASE}/api/oferente/cv`, {
        method: 'DELETE',
        headers: headers(token)
    });
    return res.json();
}

export async function getPuestosDisponibles(token) {
    const res = await fetch(`${BASE}/api/oferente/puestos`, { headers: headers(token) });
    return res.json();
}

export async function postular(puestoId, token) {
    const res = await fetch(`${BASE}/api/oferente/postulaciones`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify({ puestoId })
    });
    return res.json();
}

export async function getMisPostulaciones(token) {
    const res = await fetch(`${BASE}/api/oferente/postulaciones`, { headers: headers(token) });
    return res.json();
}

export async function buscarPuestosOferente(caracteristicaIds, moneda, token) {
    const res = await fetch(`${BASE}/api/oferente/puestos/buscar`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify({ caracteristicaIds, moneda })
    });
    return res.json();
}