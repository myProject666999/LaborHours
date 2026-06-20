import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/report',
    name: 'LaborReport',
    component: () => import('@/views/LaborReport.vue'),
    meta: { title: '工人报工' }
  },
  {
    path: '/records',
    name: 'LaborRecords',
    component: () => import('@/views/LaborRecords.vue'),
    meta: { title: '报工记录' }
  },
  {
    path: '/workers',
    name: 'WorkerList',
    component: () => import('@/views/WorkerList.vue'),
    meta: { title: '工人管理' }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('@/views/OrderList.vue'),
    meta: { title: '订单管理' }
  },
  {
    path: '/processes',
    name: 'ProcessList',
    component: () => import('@/views/ProcessList.vue'),
    meta: { title: '工序管理' }
  },
  {
    path: '/workorders',
    name: 'WorkOrderList',
    component: () => import('@/views/WorkOrderList.vue'),
    meta: { title: '工单管理' }
  },
  {
    path: '/summary/worker',
    name: 'SummaryByWorker',
    component: () => import('@/views/SummaryByWorker.vue'),
    meta: { title: '按工人汇总' }
  },
  {
    path: '/summary/order',
    name: 'SummaryByOrder',
    component: () => import('@/views/SummaryByOrder.vue'),
    meta: { title: '按订单汇总' }
  },
  {
    path: '/summary/process',
    name: 'SummaryByProcess',
    component: () => import('@/views/SummaryByProcess.vue'),
    meta: { title: '按工序汇总' }
  },
  {
    path: '/summary/compare',
    name: 'SummaryCompare',
    component: () => import('@/views/SummaryCompare.vue'),
    meta: { title: '排产对比' }
  },
  {
    path: '/realtime',
    name: 'RealtimeLabor',
    component: () => import('@/views/RealtimeLabor.vue'),
    meta: { title: '实时工时监控' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
