<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h2>工时管理系统</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">
          <el-icon><DataBoard /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/report">
          <el-icon><EditPen /></el-icon>
          <span>工人报工</span>
        </el-menu-item>
        <el-menu-item index="/records">
          <el-icon><Document /></el-icon>
          <span>报工记录</span>
        </el-menu-item>
        <el-sub-menu index="base">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>基础数据</span>
          </template>
          <el-menu-item index="/workers">工人管理</el-menu-item>
          <el-menu-item index="/orders">订单管理</el-menu-item>
          <el-menu-item index="/processes">工序管理</el-menu-item>
          <el-menu-item index="/workorders">工单管理</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="summary">
          <template #title>
            <el-icon><DataAnalysis /></el-icon>
            <span>工时汇总</span>
          </template>
          <el-menu-item index="/summary/worker">按工人汇总</el-menu-item>
          <el-menu-item index="/summary/order">按订单汇总</el-menu-item>
          <el-menu-item index="/summary/process">按工序汇总</el-menu-item>
          <el-menu-item index="/summary/compare">排产对比</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/realtime">
          <el-icon><Monitor /></el-icon>
          <span>实时监控</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <span class="breadcrumb">{{ currentRouteName }}</span>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)
const currentRouteName = computed(() => route.meta?.title || '首页')
</script>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}

.layout-container {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  overflow-y: auto;
}

.sidebar .logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b2f3a;
}

.sidebar .logo h2 {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

.sidebar .el-menu {
  border-right: none;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header .breadcrumb {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
