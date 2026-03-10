import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { rawMaterialApi, productApi, optimizationApi } from '../services/api'

const mockFetch = (body, status = 200) => {
  global.fetch = vi.fn().mockResolvedValue({
    ok: status >= 200 && status < 300,
    status,
    json: () => Promise.resolve(body),
  })
}

afterEach(() => vi.restoreAllMocks())

describe('rawMaterialApi', () => {
  it('findAll calls correct URL with no filters', async () => {
    mockFetch([])
    await rawMaterialApi.findAll()
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/raw-materials',
      expect.any(Object)
    )
  })

  it('findAll appends query params when filters provided', async () => {
    mockFetch([])
    await rawMaterialApi.findAll('flour', 'RM-001')
    const url = fetch.mock.calls[0][0]
    expect(url).toContain('name=flour')
    expect(url).toContain('code=RM-001')
  })

  it('create sends POST with correct body', async () => {
    const payload = { code: 'RM-001', name: 'Flour', stockQuantity: 1000 }
    mockFetch(payload)
    await rawMaterialApi.create(payload)
    const [, options] = fetch.mock.calls[0]
    expect(options.method).toBe('POST')
    expect(JSON.parse(options.body)).toEqual(payload)
  })

  it('update sends PUT to correct URL', async () => {
    const payload = { code: 'RM-001', name: 'Flour', stockQuantity: 500 }
    mockFetch(payload)
    await rawMaterialApi.update(1, payload)
    const [url, options] = fetch.mock.calls[0]
    expect(url).toContain('/raw-materials/1')
    expect(options.method).toBe('PUT')
  })

  it('delete sends DELETE to correct URL', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: true, status: 204, json: () => Promise.resolve(null) })
    await rawMaterialApi.delete(1)
    const [url, options] = fetch.mock.calls[0]
    expect(url).toContain('/raw-materials/1')
    expect(options.method).toBe('DELETE')
  })

  it('throws when response is not ok', async () => {
    mockFetch({ message: 'Not found' }, 404)
    await expect(rawMaterialApi.findById(999)).rejects.toThrow('Not found')
  })
})


describe('productApi', () => {
  it('findAll calls correct URL', async () => {
    mockFetch([])
    await productApi.findAll()
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/products',
      expect.any(Object)
    )
  })

  it('create sends product with ingredients', async () => {
    const payload = {
      code: 'P-001',
      name: 'Cake',
      price: 50,
      ingredients: [{ rawMaterial: { id: 1 }, requiredQuantity: 200 }],
    }
    mockFetch(payload)
    await productApi.create(payload)
    const [, options] = fetch.mock.calls[0]
    expect(JSON.parse(options.body).ingredients).toHaveLength(1)
  })

  it('delete sends DELETE to correct URL', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: true, status: 204, json: () => Promise.resolve(null) })
    await productApi.delete(5)
    expect(fetch.mock.calls[0][0]).toContain('/products/5')
  })
})


describe('optimizationApi', () => {
  it('optimize calls /production/optimize', async () => {
    mockFetch({ suggestedProducts: [], totalExpectedRevenue: 0 })
    await optimizationApi.optimize()
    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:8080/api/production/optimize',
      expect.any(Object)
    )
  })

  it('returns parsed suggestion DTO', async () => {
    const response = {
      suggestedProducts: [{ productName: 'Cake', quantityToProduce: 3, totalRevenue: 150 }],
      totalExpectedRevenue: 150,
    }
    mockFetch(response)
    const result = await optimizationApi.optimize()
    expect(result.suggestedProducts).toHaveLength(1)
    expect(result.totalExpectedRevenue).toBe(150)
  })
})
