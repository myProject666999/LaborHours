import request from '@/utils/request'

export function listLaborRecords() {
  return request({
    url: '/laborRecord/list',
    method: 'get'
  })
}

export function pageLaborRecords(params) {
  return request({
    url: '/laborRecord/page',
    method: 'get',
    params
  })
}

export function getLaborRecord(id) {
  return request({
    url: `/laborRecord/${id}`,
    method: 'get'
  })
}

export function addLaborRecord(data) {
  return request({
    url: '/laborRecord/add',
    method: 'post',
    data
  })
}

export function updateLaborRecord(data) {
  return request({
    url: '/laborRecord/update',
    method: 'post',
    data
  })
}

export function deleteLaborRecord(id) {
  return request({
    url: `/laborRecord/delete/${id}`,
    method: 'post'
  })
}

export function reportLabor(data) {
  return request({
    url: '/laborRecord/report',
    method: 'post',
    data
  })
}

export function checkOverlap(params) {
  return request({
    url: '/laborRecord/checkOverlap',
    method: 'get',
    params
  })
}

export function getTodayRecords(workerId) {
  return request({
    url: `/laborRecord/today/${workerId}`,
    method: 'get'
  })
}
