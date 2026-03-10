import { createRouter, createWebHistory } from 'vue-router'
import RawMaterialsView from '../views/RawMaterialsView.vue'
import ProductsView from '../views/ProductsView.vue'
import OptimizerView from '../views/OptimizerView.vue'

export default createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/raw-materials' },
    { path: '/raw-materials', component: RawMaterialsView },
    { path: '/products', component: ProductsView },
    { path: '/optimize', component: OptimizerView },
  ],
})
