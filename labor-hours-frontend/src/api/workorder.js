import request from '@/utils/request'

export function listWorkOrders() {
  return request({
    url: '/workOrder/list',
    method: 'get'
  })
}

export function pageWorkOrders(params) {
  return request({
    url: '/workOrder/page',
    method: 'get',
    params
  })
}

export function getWorkOrder(id) {
  return request({
    url: `/workOrder/${id}`,
    method: 'get'
  })
}

export function addWorkOrder(data) {
  return request({
    url: '/workOrder/add',
    method: 'post',
    data
  })
}

export function updateWorkOrder(data) {
  return request({
    url: '/workOrder/update',
    method: 'post',
    data
  })
}

export function deleteWorkOrder(id) {
  return request({
    url: `/workOrder/${id}`,
    method: 'delete'
  })
}
