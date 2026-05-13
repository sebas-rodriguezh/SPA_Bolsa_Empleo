const BASE = import.meta.env.VITE_API_URL;

function headers(token) {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

export async function getEmpresaDashboard(token) {
    const res = await fetch(`${BASE}/api/empresa/dashboard`, { headers: headers(token) });
    return res.json();
}

export async function getMisPuestos(token) {
    const res = await fetch(`${BASE}/api/empresa/puestos`, { headers: headers(token) });
    return res.json();
}

export async function crearPuesto(datos, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify(datos)
    });
    return res.json();
}

export async function desactivarPuesto(id, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${id}/desactivar`, {
        method: 'POST',
        headers: headers(token)
    });
    return res.json();
}

export async function getRequisitos(puestoId, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${puestoId}/requisitos`, { headers: headers(token) });
    return res.json();
}

export async function agregarRequisito(puestoId, datos, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${puestoId}/requisitos`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify(datos)
    });
    return res.json();
}

export async function quitarRequisito(puestoId, pcId, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${puestoId}/requisitos/${pcId}`, {
        method: 'DELETE',
        headers: headers(token)
    });
    return res.json();
}

export async function getCandidatos(puestoId, modo, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${puestoId}/candidatos?modo=${modo}`, { headers: headers(token) });
    return res.json();
}

export async function getDetalleCandidato(oferenteId, puestoId, token) {
    const res = await fetch(`${BASE}/api/empresa/candidatos/${oferenteId}?puestoId=${puestoId}`, { headers: headers(token) });
    return res.json();
}

export async function getPostulaciones(puestoId, token) {
    const res = await fetch(`${BASE}/api/empresa/puestos/${puestoId}/postulaciones`, { headers: headers(token) });
    return res.json();
}

export async function getReporteEmpresa(token, puestoId = null) {
    let url = `${BASE}/api/empresa/reportes`;
    if (puestoId) url += `?puestoId=${puestoId}`;
    const res = await fetch(url, { headers: headers(token) });
    return res.json();
}