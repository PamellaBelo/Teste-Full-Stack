<template>
  <div id="app">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-mark">FACTORY<span>.</span>OPT</div>
        <div class="logo-sub">Otimizador de Produção</div>
      </div>

      <nav class="sidebar-nav">
        <div class="nav-section-label">Estoque</div>
        <RouterLink to="/raw-materials" class="nav-link">
          <span class="nav-icon">📦</span> {{ $t('nav.rawMaterials') }}
        </RouterLink>

        <div class="nav-section-label">Cadastro</div>
        <RouterLink to="/products" class="nav-link">
          <span class="nav-icon">🏭</span> {{ $t('nav.products') }}
        </RouterLink>

        <div class="nav-section-label">Análise</div>
        <RouterLink to="/optimize" class="nav-link">
          <span class="nav-icon">⚡</span> {{ $t('nav.optimize') }}
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div class="lang-label">Idioma / Language</div>
        <div class="lang-toggle">
          <button class="lang-btn" :class="{ active: locale === 'pt-BR' }" @click="setLocale('pt-BR')">PT</button>
          <button class="lang-btn" :class="{ active: locale === 'en' }" @click="setLocale('en')">EN</button>
        </div>
      </div>
    </aside>

    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
const { locale } = useI18n()
function setLocale(lang) { locale.value = lang }
</script>
