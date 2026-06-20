import request from '@/utils/request'

export function listOrders() {
  return request({
    url: '/order/list',
    method: 'get'
  })
}

export function pageOrders(params) {
  return request({
    url: '/order/page',
    method: 'get',
    params
  })
}

export function getOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

export function addOrder(data) {
  return request({
    url: '/order/add',
    method: 'post',
    data
  })
}

export function updateOrder(data) {
  return request({
    url: '/order/update',
    method: 'post',
    data
  })
}

export function deleteOrder(id) {
  return request({
    url: `/order/${id}`,
    method: 'delete'
  })
}
