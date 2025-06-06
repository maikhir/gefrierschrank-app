import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import CategoriesView from '../views/CategoriesView.vue'
import LocationsView from '../views/LocationsView.vue'
import SettingsView from '../views/SettingsView.vue'
import BackupView from '../views/BackupView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'dashboard',
      component: Dashboard,
    },
    {
      path: '/categories',
      name: 'categories',
      component: CategoriesView,
      meta: {
        title: 'Kategorien'
      }
    },
    {
      path: '/locations',
      name: 'locations',
      component: LocationsView,
      meta: {
        title: 'Standorte'
      }
    },
    {
      path: '/settings',
      name: 'settings',
      component: SettingsView,
      meta: {
        title: 'Einstellungen'
      }
    },
    {
      path: '/backup',
      name: 'backup',
      component: BackupView,
      meta: {
        title: 'Datensicherung'
      }
    },
  ],
})

export default router