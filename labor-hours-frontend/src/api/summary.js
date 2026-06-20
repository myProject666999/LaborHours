import request from '@/utils/request'

export function summaryByWorker(data) {
  return request({
    url: '/summary/byWorker',
    method: 'post',
    data
  })
}

export function summaryByOrder(data) {
  return request({
    url: '/summary/byOrder',
    method: 'post',
    data
  })
}

export function summaryByProcess(data) {
  return request({
    url: '/summary/byProcess',
    method: 'post',
    data
  })
}

export function summaryByWorkerDateRange(params) {
  return request({
    url: '/summary/byWorker/dateRange',
    method: 'get',
    params
  })
}

export function summaryByOrderDateRange(params) {
  return request({
    url: '/summary/byOrder/dateRange',
    method: 'get',
    params
  })
}

export function summaryByProcessDateRange(params) {
  return request({
    url: '/summary/byProcess/dateRange',
    method: 'get',
    params
  })
}

export function getTodayRealtimeSummary() {
  return request({
    url: '/summary/today/realtime',
    method: 'get'
  })
}

export function summaryByWorkerAndDate(workerId, date) {
  return request({
    url: `/summary/worker/${workerId}/date/${date}`,
    method: 'get'
  })
}

export function compareWorkOrder(params) {
  return request({
    url: '/summary/compare/workOrder',
    method: 'get',
    params
  })
}
