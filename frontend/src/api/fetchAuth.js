export async function fetchAuth(url, options = {}, token = null) {
    const res = await fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {})
        }
    });

    if (res.status === 401) {
        localStorage.clear();
        window.dispatchEvent(new CustomEvent('sesion-expirada'));
        return null;
    }

    return res.json();
}