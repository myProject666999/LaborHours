import request from '@/utils/request'

export function getTodayLaborHours(workerId) {
  return request({
    url: `/redisLabor/today/${workerId}`,
    method: 'get'
  })
}

export function getTodayLaborHoursBatch(data) {
  return request({
    url: '/redisLabor/today/batch',
    method: 'post',
    data
  })
}

export function getAllTodayLaborHours() {
  return request({
    url: '/redisLabor/today/all',
    method: 'get'
  })
}

export function refreshTodayLaborCache(workerId) {
  return request({
    url: `/redisLabor/refresh/${workerId}`,
    method: 'post'
  })
}

export function deleteTodayLaborCache(workerId) {
  return request({
    url: `/redisLabor/delete/${workerId}`,
    method: 'post'
  })
}
