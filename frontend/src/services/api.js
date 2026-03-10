const BASE_URL = 'http://localhost:8080/api'

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.message || `HTTP ${res.status}`)
  }
  if (res.status === 204) return null
  return res.json()
}

export const rawMaterialApi = {
  findAll: (name, code) => {
    const params = new URLSearchParams()
    if (name) params.append('name', name)
    if (code) params.append('code', code)
    const q = params.toString()
    return request(`/raw-materials${q ? '?' + q : ''}`)
  },
  findById: (id) => request(`/raw-materials/${id}`),
  create: (data) => request('/raw-materials', { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`/raw-materials/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => request(`/raw-materials/${id}`, { method: 'DELETE' }),
}

export const productApi = {
  findAll: (name, code) => {
    const params = new URLSearchParams()
    if (name) params.append('name', name)
    if (code) params.append('code', code)
    const q = params.toString()
    return request(`/products${q ? '?' + q : ''}`)
  },
  findById: (id) => request(`/products/${id}`),
  create: (data) => request('/products', { method: 'POST', body: JSON.stringify(data) }),
  update: (id, data) => request(`/products/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id) => request(`/products/${id}`, { method: 'DELETE' }),
}

export const optimizationApi = {
  optimize: () => request('/production/optimize'),
}
