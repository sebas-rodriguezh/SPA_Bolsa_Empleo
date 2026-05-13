const BASE = import.meta.env.VITE_API_URL;

function headers(token) {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

export async function getEmpresasPendientes(token) {
    const res = await fetch(`${BASE}/api/admin/empresas/pendientes`, { headers: headers(token) });
    return res.json();
}

export async function aprobarEmpresa(id, token) {
    const res = await fetch(`${BASE}/api/admin/empresas/${id}/aprobar`, {
        method: 'POST',
        headers: headers(token)
    });
    return res.json();
}

export async function getOferentesPendientes(token) {
    const res = await fetch(`${BASE}/api/admin/oferentes/pendientes`, { headers: headers(token) });
    return res.json();
}

export async function aprobarOferente(id, token) {
    const res = await fetch(`${BASE}/api/admin/oferentes/${id}/aprobar`, {
        method: 'POST',
        headers: headers(token)
    });
    return res.json();
}

export async function getCaracteristicas(token) {
    const res = await fetch(`${BASE}/api/admin/caracteristicas`, { headers: headers(token) });
    return res.json();
}

export async function crearCaracteristica(datos, token) {
    const res = await fetch(`${BASE}/api/admin/caracteristicas`, {
        method: 'POST',
        headers: headers(token),
        body: JSON.stringify(datos)
    });
    return res.json();
}

export async function getReportes(token, mes = null, anio = null) {
    let url = `${BASE}/api/admin/reportes`;
    if (mes && anio) url += `?mes=${mes}&anio=${anio}`;
    const res = await fetch(url, { headers: headers(token) });
    return res.json();
}