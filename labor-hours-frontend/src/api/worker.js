import request from '@/utils/request'

export function listWorkers() {
  return request({
    url: '/worker/list',
    method: 'get'
  })
}

export function pageWorkers(params) {
  return request({
    url: '/worker/page',
    method: 'get',
    params
  })
}

export function getWorker(id) {
  return request({
    url: `/worker/${id}`,
    method: 'get'
  })
}

export function addWorker(data) {
  return request({
    url: '/worker/add',
    method: 'post',
    data
  })
}

export function updateWorker(data) {
  return request({
    url: '/worker/update',
    method: 'post',
    data
  })
}

export function deleteWorker(id) {
  return request({
    url: `/worker/${id}`,
    method: 'delete'
  })
}
