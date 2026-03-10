<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">{{ $t('optimizer.title') }} <span>_</span></h1>
    </div>

    <div class="optimizer-card">
      <div class="optimizer-hero">
        <div class="optimizer-hero-text">
          <h2>{{ $t('optimizer.title') }}</h2>
          <p>{{ $t('optimizer.subtitle') }}</p>
        </div>

        <div style="display:flex;flex-direction:column;align-items:flex-end;gap:16px">
          <div class="revenue-display" v-if="result">
            <div class="revenue-label">{{ $t('optimizer.totalRevenue') }}</div>
            <div class="revenue-value">R$ {{ formatCurrency(result.totalExpectedRevenue) }}</div>
          </div>
          <button class="btn btn-accent btn-lg" @click="runOptimization" :disabled="loading">
            <span v-if="loading" class="spinner" style="border-top-color:#fff;border-color:rgba(255,255,255,0.3)"></span>
            <span v-else>◎</span>
            {{ loading ? $t('optimizer.running') : $t('optimizer.run') }}
          </button>
        </div>
      </div>

      <div>
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>{{ $t('optimizer.productName') }}</th>
              <th>{{ $t('optimizer.quantity') }}</th>
              <th>{{ $t('optimizer.revenue') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!result && !loading" class="empty-row">
              <td colspan="4">{{ $t('optimizer.run') }} →</td>
            </tr>
            <tr v-else-if="loading" class="empty-row">
              <td colspan="4">{{ $t('optimizer.running') }}</td>
            </tr>
            <tr v-else-if="result && !result.suggestedProducts.length" class="empty-row">
              <td colspan="4">{{ $t('optimizer.noSuggestions') }}</td>
            </tr>
            <tr
              v-for="(item, idx) in result?.suggestedProducts"
              :key="idx"
              class="suggestion-row"
            >
              <td style="color:var(--text3);font-size:0.75rem">{{ String(idx + 1).padStart(2, '0') }}</td>
              <td>{{ item.productName }}</td>
              <td class="suggestion-qty">× {{ item.quantityToProduce }}</td>
              <td class="suggestion-revenue">R$ {{ formatCurrency(item.totalRevenue) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="toast" :class="['toast', `toast-${toast.type}`]">{{ toast.msg }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { optimizationApi } from '../services/api'

const { t } = useI18n()
const result = ref(null)
const loading = ref(false)
const toast = ref(null)

async function runOptimization() {
  loading.value = true
  result.value = null
  try {
    result.value = await optimizationApi.optimize()
  } catch (e) {
    showToast(e.message || t('common.error'), 'error')
  } finally {
    loading.value = false
  }
}

function formatCurrency(value) {
  return Number(value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function showToast(msg, type = 'success') {
  toast.value = { msg, type }
  setTimeout(() => (toast.value = null), 3000)
}
</script>
