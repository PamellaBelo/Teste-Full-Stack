import { describe, it, expect, vi, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createI18n } from 'vue-i18n'
import OptimizerView from '../views/OptimizerView.vue'
import * as apiModule from '../services/api'

const i18n = createI18n({
  legacy: false,
  locale: 'en',
  messages: {
    en: {
      optimizer: {
        title: 'Production Optimizer',
        subtitle: 'Maximize revenue',
        run: 'Run Optimization',
        running: 'Analyzing...',
        productName: 'Product',
        quantity: 'Qty',
        revenue: 'Revenue',
        totalRevenue: 'Total Revenue',
        noSuggestions: 'No products can be produced.',
      },
      common: { error: 'An error occurred.' },
    },
  },
})

const mountComponent = () =>
  mount(OptimizerView, { global: { plugins: [i18n] } })

afterEach(() => vi.restoreAllMocks())

describe('OptimizerView', () => {
  it('renders run button', () => {
    const wrapper = mountComponent()
    expect(wrapper.find('button').text()).toContain('Run Optimization')
  })

  it('shows loading state while fetching', async () => {
    vi.spyOn(apiModule, 'optimizationApi', 'get').mockReturnValue({
      optimize: () => new Promise(() => {}), // never resolves
    })
    const wrapper = mountComponent()
    await wrapper.find('button').trigger('click')
    expect(wrapper.text()).toContain('Analyzing')
  })

  it('renders suggested products after successful optimization', async () => {
    vi.spyOn(apiModule, 'optimizationApi', 'get').mockReturnValue({
      optimize: vi.fn().mockResolvedValue({
        suggestedProducts: [
          { productName: 'Cake', quantityToProduce: 3, totalRevenue: 150 },
          { productName: 'Cookie', quantityToProduce: 5, totalRevenue: 50 },
        ],
        totalExpectedRevenue: 200,
      }),
    })
    const wrapper = mountComponent()
    await wrapper.find('button').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Cake')
    expect(wrapper.text()).toContain('Cookie')
    expect(wrapper.text()).toContain('200')
  })

  it('shows empty message when no products can be produced', async () => {
    vi.spyOn(apiModule, 'optimizationApi', 'get').mockReturnValue({
      optimize: vi.fn().mockResolvedValue({
        suggestedProducts: [],
        totalExpectedRevenue: 0,
      }),
    })
    const wrapper = mountComponent()
    await wrapper.find('button').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('No products can be produced')
  })

  it('shows error toast when API fails', async () => {
    vi.spyOn(apiModule, 'optimizationApi', 'get').mockReturnValue({
      optimize: vi.fn().mockRejectedValue(new Error('Server error')),
    })
    const wrapper = mountComponent()
    await wrapper.find('button').trigger('click')
    await flushPromises()

    expect(wrapper.text()).toContain('Server error')
  })

  it('disables button while loading', async () => {
    vi.spyOn(apiModule, 'optimizationApi', 'get').mockReturnValue({
      optimize: () => new Promise(() => {}),
    })
    const wrapper = mountComponent()
    await wrapper.find('button').trigger('click')
    expect(wrapper.find('button').attributes('disabled')).toBeDefined()
  })
})
