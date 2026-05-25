const BASE = import.meta.env.VITE_API_URL;
import { fetchAuth } from './fetchAuth';

export async function getOferenteDashboard(token) {
    return fetchAuth(`${BASE}/api/oferente/dashboard`, {}, token);
}

export async function getHabilidades(token) {
    return fetchAuth(`${BASE}/api/oferente/habilidades`, {}, token);
}

export async function agregarHabilidad(datos, token) {
    return fetchAuth(`${BASE}/api/oferente/habilidades`, {
        method: 'POST',
        body: JSON.stringify(datos)
    }, token);
}

export async function eliminarHabilidad(id, token) {
    return fetchAuth(`${BASE}/api/oferente/habilidades/${id}`, {
        method: 'DELETE'
    }, token);
}

export async function getCV(token) {
    return fetchAuth(`${BASE}/api/oferente/cv`, {}, token);
}

export async function actualizarCV(rutaCurriculum, token) {
    return fetchAuth(`${BASE}/api/oferente/cv`, {
        method: 'PUT',
        body: JSON.stringify({ rutaCurriculum })
    }, token);
}

export async function eliminarCV(token) {
    return fetchAuth(`${BASE}/api/oferente/cv`, {
        method: 'DELETE'
    }, token);
}

export async function getPuestosDisponibles(token) {
    return fetchAuth(`${BASE}/api/oferente/puestos`, {}, token);
}

export async function postular(puestoId, token) {
    return fetchAuth(`${BASE}/api/oferente/postulaciones`, {
        method: 'POST',
        body: JSON.stringify({ puestoId })
    }, token);
}

export async function getMisPostulaciones(token) {
    return fetchAuth(`${BASE}/api/oferente/postulaciones`, {}, token);
}

export async function buscarPuestosOferente(caracteristicaIds, moneda, token) {
    return fetchAuth(`${BASE}/api/oferente/puestos/buscar`, {
        method: 'POST',
        body: JSON.stringify({ caracteristicaIds, moneda })
    }, token);
}