import { describe, it, expect, vi, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createI18n } from 'vue-i18n'
import RawMaterialsView from '../views/RawMaterialsView.vue'
import * as apiModule from '../services/api'

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      rawMaterial: {
        title: 'Raw Materials',
        newTitle: 'New Raw Material',
        editTitle: 'Edit Raw Material',
        stockQuantity: 'Stock Quantity',
      },
      common: {
        code: 'Code', name: 'Name', actions: 'Actions',
        add: 'Add', edit: 'Edit', delete: 'Delete',
        save: 'Save', cancel: 'Cancel', search: 'Search',
        loading: 'Loading...', noData: 'No records.',
        confirmDelete: 'Are you sure?', success: 'Saved!', deleted: 'Deleted!',
        error: 'Error.',
      },
    },
  },
})

const mockApi = (items = []) => ({
  findAll: vi.fn().mockResolvedValue(items),
  create: vi.fn().mockResolvedValue({ id: 99, code: 'RM-NEW', name: 'New', stockQuantity: 100 }),
  update: vi.fn().mockResolvedValue({}),
  delete: vi.fn().mockResolvedValue(null),
  findById: vi.fn(),
})

const mountComponent = () =>
  mount(RawMaterialsView, { global: { plugins: [i18n] } })

afterEach(() => vi.restoreAllMocks())

describe('RawMaterialsView', () => {
  it('shows loading then renders items', async () => {
    const items = [
      { id: 1, code: 'RM-001', name: 'Flour', stockQuantity: 1000 },
      { id: 2, code: 'RM-002', name: 'Sugar', stockQuantity: 500 },
    ]
    vi.spyOn(apiModule, 'rawMaterialApi', 'get').mockReturnValue(mockApi(items))

    const wrapper = mountComponent()
    await flushPromises()

    expect(wrapper.text()).toContain('Flour')
    expect(wrapper.text()).toContain('Sugar')
    expect(wrapper.text()).toContain('RM-001')
  })

  it('shows empty state when no items', async () => {
    vi.spyOn(apiModule, 'rawMaterialApi', 'get').mockReturnValue(mockApi([]))
    const wrapper = mountComponent()
    await flushPromises()
    expect(wrapper.text()).toContain('No records')
  })

  it('opens create modal when Add is clicked', async () => {
    vi.spyOn(apiModule, 'rawMaterialApi', 'get').mockReturnValue(mockApi([]))
    const wrapper = mountComponent()
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    expect(wrapper.find('.modal').exists()).toBe(true)
    expect(wrapper.text()).toContain('New Raw Material')
  })

  it('opens edit modal with prefilled data', async () => {
    const items = [{ id: 1, code: 'RM-001', name: 'Flour', stockQuantity: 1000 }]
    vi.spyOn(apiModule, 'rawMaterialApi', 'get').mockReturnValue(mockApi(items))

    const wrapper = mountComponent()
    await flushPromises()

    await wrapper.find('.btn-secondary').trigger('click')
    expect(wrapper.find('.modal').exists()).toBe(true)
    expect(wrapper.text()).toContain('Edit Raw Material')

    const inputs = wrapper.findAll('input.input')
    expect(inputs[0].element.value).toBe('RM-001')
    expect(inputs[1].element.value).toBe('Flour')
  })

  it('closes modal on cancel', async () => {
    vi.spyOn(apiModule, 'rawMaterialApi', 'get').mockReturnValue(mockApi([]))
    const wrapper = mountComponent()
    await flushPromises()

    await wrapper.find('.btn-primary').trigger('click')
    expect(wrapper.find('.modal').exists()).toBe(true)

    await wrapper.find('.btn-secondary.btn').trigger('click')
  })
})
