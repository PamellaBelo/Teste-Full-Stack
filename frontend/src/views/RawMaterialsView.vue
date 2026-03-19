<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">{{ $t('rawMaterial.title') }} <span>_</span></h1>
    </div>

    <!-- Toolbar -->
    <div class="toolbar">
      <input
        class="input"
        style="max-width:200px"
        :placeholder="$t('common.name')"
        v-model="filterName"
        @input="fetchData"
      />
      <input
        class="input"
        style="max-width:160px"
        :placeholder="$t('common.code')"
        v-model="filterCode"
        @input="fetchData"
      />
      <button class="btn btn-primary" @click="openCreate">
        + {{ $t('common.add') }}
      </button>
    </div>

    <!-- Table -->
    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>{{ $t('common.code') }}</th>
            <th>{{ $t('common.name') }}</th>
            <th>{{ $t('rawMaterial.stockQuantity') }}</th>
            <th>{{ $t('common.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="empty-row">
            <td colspan="4">{{ $t('common.loading') }}</td>
          </tr>
          <tr v-else-if="!items.length" class="empty-row">
            <td colspan="4">{{ $t('common.noData') }}</td>
          </tr>
          <tr v-for="item in items" :key="item.id">
            <td class="td-code">{{ item.code }}</td>
            <td>{{ item.name }}</td>
            <td>{{ item.stockQuantity }}</td>
            <td class="td-actions">
              <button class="btn btn-secondary btn-sm" @click="openEdit(item)">
                {{ $t('common.edit') }}
              </button>
              <button class="btn btn-danger btn-sm" @click="confirmDelete(item)">
                {{ $t('common.delete') }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div class="modal-overlay" v-if="showModal" @click.self="showModal = false">
      <div class="modal">
        <div class="modal-header">
          <span class="modal-title">
            {{ editing ? $t('rawMaterial.editTitle') : $t('rawMaterial.newTitle') }}
          </span>
          <button class="modal-close" @click="showModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-grid">
            <div class="form-row">
              <div class="input-group">
                <label class="input-label">{{ $t('common.code') }}</label>
                <input class="input" v-model="form.code" />
              </div>
              <div class="input-group">
                <label class="input-label">{{ $t('common.name') }}</label>
                <input class="input" v-model="form.name" />
              </div>
            </div>
            <div class="input-group">
              <label class="input-label">{{ $t('rawMaterial.stockQuantity') }}</label>
              <input class="input" type="number" step="0.01" v-model.number="form.stockQuantity" />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showModal = false">{{ $t('common.cancel') }}</button>
          <button class="btn btn-primary" @click="save" :disabled="saving">
            <span v-if="saving" class="spinner"></span>
            <span v-else>{{ $t('common.save') }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <div v-if="toast" :class="['toast', `toast-${toast.type}`]">{{ toast.msg }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { rawMaterialApi } from '../services/api'

const { t } = useI18n()
const items = ref([])
const loading = ref(false)
const saving = ref(false)
const showModal = ref(false)
const editing = ref(null)
const filterName = ref('')
const filterCode = ref('')
const toast = ref(null)

const emptyForm = () => ({ code: '', name: '', stockQuantity: 0 })
const form = ref(emptyForm())

async function fetchData() {
  loading.value = true
  try {
    items.value = await rawMaterialApi.findAll(filterName.value, filterCode.value)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editing.value = null
  form.value = emptyForm()
  showModal.value = true
}

function openEdit(item) {
  editing.value = item
  form.value = { ...item }
  showModal.value = true
}

async function save() {
  saving.value = true
  try {
    if (editing.value) {
      await rawMaterialApi.update(editing.value.id, form.value)
    } else {
      await rawMaterialApi.create(form.value)
    }
    showModal.value = false
    showToast(t('common.success'), 'success')
    fetchData()
  } catch (e) {
    showToast(e.message || t('common.error'), 'error')
  } finally {
    saving.value = false
  }
}

async function confirmDelete(item) {
  if (!confirm(t('common.confirmDelete'))) return
  try {
    await rawMaterialApi.delete(item.id)
    showToast(t('common.deleted'), 'success')
    fetchData()
  } catch (e) {
    showToast(e.message || t('common.error'), 'error')
  }
}

function showToast(msg, type = 'success') {
  toast.value = { msg, type }
  setTimeout(() => (toast.value = null), 3000)
}

onMounted(fetchData)
</script>
