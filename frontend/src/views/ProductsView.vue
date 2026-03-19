<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">{{ $t('product.title') }} <span>_</span></h1>
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
            <th>{{ $t('product.price') }}</th>
            <th>{{ $t('product.ingredients') }}</th>
            <th>{{ $t('common.actions') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading" class="empty-row">
            <td colspan="5">{{ $t('common.loading') }}</td>
          </tr>
          <tr v-else-if="!items.length" class="empty-row">
            <td colspan="5">{{ $t('common.noData') }}</td>
          </tr>
          <tr v-for="item in items" :key="item.id">
            <td class="td-code">{{ item.code }}</td>
            <td>{{ item.name }}</td>
            <td>R$ {{ Number(item.price).toFixed(2) }}</td>
            <td>
              <span
                v-for="ing in item.ingredients"
                :key="ing.id"
                class="badge badge-blue"
                style="margin-right:4px"
              >
                {{ ing.rawMaterial?.name }} ({{ ing.requiredQuantity }})
              </span>
              <span v-if="!item.ingredients?.length" style="color:var(--text3)">—</span>
            </td>
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
      <div class="modal" style="max-width:640px">
        <div class="modal-header">
          <span class="modal-title">
            {{ editing ? $t('product.editTitle') : $t('product.newTitle') }}
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
              <label class="input-label">{{ $t('product.price') }}</label>
              <input class="input" type="number" step="0.01" v-model.number="form.price" />
            </div>

            <!-- Ingredients -->
            <div>
              <div class="ingredients-header">
                <span class="ingredients-label">{{ $t('product.ingredients') }}</span>
                <button class="btn btn-secondary btn-sm" @click="addIngredient">
                  + {{ $t('product.addIngredient') }}
                </button>
              </div>

              <p v-if="!form.ingredients.length" style="color:var(--text3);font-size:0.78rem">
                {{ $t('product.noIngredients') }}
              </p>

              <div
                v-for="(ing, idx) in form.ingredients"
                :key="idx"
                class="ingredient-row"
              >
                <select class="input" v-model="ing.rawMaterialId">
                  <option disabled value="">{{ $t('product.rawMaterial') }}</option>
                  <option v-for="rm in rawMaterials" :key="rm.id" :value="rm.id">
                    {{ rm.name }} ({{ rm.code }})
                  </option>
                </select>
                <input
                  class="input"
                  type="number"
                  step="0.01"
                  :placeholder="$t('product.requiredQuantity')"
                  v-model.number="ing.requiredQuantity"
                />
                <button class="btn btn-danger btn-sm" @click="removeIngredient(idx)">✕</button>
              </div>
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

    <div v-if="toast" :class="['toast', `toast-${toast.type}`]">{{ toast.msg }}</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { productApi, rawMaterialApi } from '../services/api'

const { t } = useI18n()
const items = ref([])
const rawMaterials = ref([])
const loading = ref(false)
const saving = ref(false)
const showModal = ref(false)
const editing = ref(null)
const filterName = ref('')
const filterCode = ref('')
const toast = ref(null)

const emptyForm = () => ({ code: '', name: '', price: 0, ingredients: [] })
const form = ref(emptyForm())

async function fetchData() {
  loading.value = true
  try {
    items.value = await productApi.findAll(filterName.value, filterCode.value)
  } finally {
    loading.value = false
  }
}

async function fetchRawMaterials() {
  rawMaterials.value = await rawMaterialApi.findAll()
}

function openCreate() {
  editing.value = null
  form.value = emptyForm()
  showModal.value = true
}

function openEdit(item) {
  editing.value = item
  form.value = {
    code: item.code,
    name: item.name,
    price: item.price,
    ingredients: (item.ingredients || []).map(ing => ({
      rawMaterialId: ing.rawMaterial?.id || '',
      requiredQuantity: ing.requiredQuantity,
    })),
  }
  showModal.value = true
}

function addIngredient() {
  form.value.ingredients.push({ rawMaterialId: '', requiredQuantity: 0 })
}

function removeIngredient(idx) {
  form.value.ingredients.splice(idx, 1)
}

function buildPayload() {
  return {
    code: form.value.code,
    name: form.value.name,
    price: form.value.price,
    ingredients: form.value.ingredients.map(ing => ({
      rawMaterial: { id: ing.rawMaterialId },
      requiredQuantity: ing.requiredQuantity,
    })),
  }
}

async function save() {
  saving.value = true
  try {
    const payload = buildPayload()
    if (editing.value) {
      await productApi.update(editing.value.id, payload)
    } else {
      await productApi.create(payload)
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
    await productApi.delete(item.id)
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

onMounted(() => {
  fetchData()
  fetchRawMaterials()
})
</script>
