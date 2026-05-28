const BASE = import.meta.env.VITE_API_URL;
import { fetchAuth } from './fetchAuth';

export async function getEmpresasPendientes(token) {
    return fetchAuth(`${BASE}/api/admin/empresas/pendientes`, {}, token);
}

export async function aprobarEmpresa(id, token) {
    return fetchAuth(`${BASE}/api/admin/empresas/${id}/aprobar`, {
        method: 'POST'
    }, token);
}

export async function getOferentesPendientes(token) {
    return fetchAuth(`${BASE}/api/admin/oferentes/pendientes`, {}, token);
}

export async function aprobarOferente(id, token) {
    return fetchAuth(`${BASE}/api/admin/oferentes/${id}/aprobar`, {
        method: 'POST'
    }, token);
}

export async function getCaracteristicas(token) {
    return fetchAuth(`${BASE}/api/admin/caracteristicas`, {}, token);
}

export async function crearCaracteristica(datos, token) {
    return fetchAuth(`${BASE}/api/admin/caracteristicas`, {
        method: 'POST',
        body: JSON.stringify(datos)
    }, token);
}

export async function eliminarCaracteristica(id, token) {
    return fetchAuth(`${BASE}/api/admin/caracteristicas/${id}`, {
        method: 'DELETE'
    }, token);
}

export async function getReportes(token, mes = null, anio = null) {
    let url = `${BASE}/api/admin/reportes`;
    if (mes && anio) url += `?mes=${mes}&anio=${anio}`;
    return fetchAuth(url, {}, token);
}