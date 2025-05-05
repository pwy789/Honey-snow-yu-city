import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout/Layout.vue'
import TasteCategory from '@/views/Category/TasteCategory.vue'
import SkuCategory from '@/views/Category/SkuCategory.vue'
import MilkyTea from '@/views/Milkytea/MilkyTea.vue'
import MilkyTeaDetail from '@/views/Milkytea/components/MilkyTeaDetail.vue'
import Orders from '@/views/Orders/Orders.vue'
import Login from '@/views/Login/Login.vue'
import Voucher from '@/views/Voucher/Voucher.vue'
import Statistics from '@/views/Statistics/Statistics.vue'
import Shop from '@/views/Shop/Shop.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect:'/login'
    },
    {
    path:'/login',
    component:Login
    },
    {
      path: '/layout',
      component: Layout,
      redirect:'/statistics',
      children:[
        {
          path:'/tastecategory',
          component:TasteCategory
        },
        {
          path:'/skucategory',
          component:SkuCategory
        },
        {
          path:'/milkytea',
          component:MilkyTea
        },
        {
          path:'/milkytea/detail/:id?',
          component:MilkyTeaDetail
        },
        {
          path:'/orders',
          component:Orders
        },
        {
          path:'/voucher',
          component:Voucher
        },
        {
          path:'/statistics',
          component:Statistics
        },
        {
          path:'/shop',
          component:Shop
        }
      ],
     
      }
  ],
})

export default router
