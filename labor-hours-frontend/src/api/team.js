import request from '@/utils/request'

export function listTeams() {
  return request({
    url: '/team/list',
    method: 'get'
  })
}

export function pageTeams(params) {
  return request({
    url: '/team/page',
    method: 'get',
    params
  })
}

export function getTeam(id) {
  return request({
    url: `/team/${id}`,
    method: 'get'
  })
}

export function addTeam(data) {
  return request({
    url: '/team/add',
    method: 'post',
    data
  })
}

export function updateTeam(data) {
  return request({
    url: '/team/update',
    method: 'post',
    data
  })
}

export function deleteTeam(id) {
  return request({
    url: `/team/${id}`,
    method: 'delete'
  })
}
