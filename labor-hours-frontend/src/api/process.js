import request from '@/utils/request'

export function listProcesses() {
  return request({
    url: '/process/list',
    method: 'get'
  })
}

export function pageProcesses(params) {
  return request({
    url: '/process/page',
    method: 'get',
    params
  })
}

export function getProcess(id) {
  return request({
    url: `/process/${id}`,
    method: 'get'
  })
}

export function addProcess(data) {
  return request({
    url: '/process/add',
    method: 'post',
    data
  })
}

export function updateProcess(data) {
  return request({
    url: '/process/update',
    method: 'post',
    data
  })
}

export function deleteProcess(id) {
  return request({
    url: `/process/${id}`,
    method: 'delete'
  })
}
