const BASE = import.meta.env.VITE_API_URL;

export async function getPuestosRecientes() {
    const res = await fetch(`${BASE}/api/publico/puestos/recientes`);
    return res.json();
}

export async function getCaracteristicasPublico() {
    const res = await fetch(`${BASE}/api/publico/caracteristicas`);
    return res.json();
}

export async function buscarPuestosPublicos(caracteristicaIds, moneda = '') {
    const res = await fetch(`${BASE}/api/publico/puestos/buscar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ caracteristicaIds, moneda })
    });
    return res.json();
}