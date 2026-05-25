const BASE = import.meta.env.VITE_API_URL;
import { fetchAuth } from './fetchAuth';

function headers(token) {
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

export async function getEmpresaDashboard(token) {
    return fetchAuth(`${BASE}/api/empresa/dashboard`, {}, token);
}

export async function getMisPuestos(token) {
    return fetchAuth(`${BASE}/api/empresa/puestos`, {}, token);
}
export async function crearPuesto(datos, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos`, {
        method: 'POST',
        body: JSON.stringify(datos)
    }, token);
}

export async function desactivarPuesto(id, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${id}/desactivar`, {
        method: 'POST'
    }, token);
}

export async function getRequisitos(puestoId, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${puestoId}/requisitos`, {}, token);
}

export async function agregarRequisito(puestoId, datos, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${puestoId}/requisitos`, {
        method: 'POST',
        body: JSON.stringify(datos)
    }, token);
}

export async function editarPuesto(id, datos, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(datos)
    }, token);
}

export async function quitarRequisito(puestoId, pcId, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${puestoId}/requisitos/${pcId}`, {
        method: 'DELETE'
    }, token);
}

export async function getCandidatos(puestoId, modo, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${puestoId}/candidatos?modo=${modo}`, {}, token);
}

export async function getDetalleCandidato(oferenteId, puestoId, token) {
    return fetchAuth(`${BASE}/api/empresa/candidatos/${oferenteId}?puestoId=${puestoId}`, {}, token);
}

export async function getPostulaciones(puestoId, token) {
    return fetchAuth(`${BASE}/api/empresa/puestos/${puestoId}/postulaciones`, {}, token);
}

export async function getReporteEmpresa(token, puestoId = null) {
    const url = puestoId
        ? `${BASE}/api/empresa/reportes?puestoId=${puestoId}`
        : `${BASE}/api/empresa/reportes`;
    return fetchAuth(url, {}, token);
}

