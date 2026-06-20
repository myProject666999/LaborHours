<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">实时工时监控</span>
        <div>
          <el-tag type="info" style="margin-right: 10px;">
            更新时间：{{ lastUpdateTime }}
          </el-tag>
          <el-switch
            v-model="autoRefresh"
            active-text="自动刷新"
            style="margin-right: 10px;"
          />
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>手动刷新
          </el-button>
        </div>
      </div>

      <el-row :gutter="20" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon :size="36" color="#409EFF"><User /></el-icon>
              <div class="stat-info">
                <div class="stat-label">在线工人</div>
                <div class="stat-value">{{ onlineWorkerCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon :size="36" color="#67C23A"><Timer /></el-icon>
              <div class="stat-info">
                <div class="stat-label">今日总工时</div>
                <div class="stat-value">{{ totalWorkHours.toFixed(2) }} h</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon :size="36" color="#E6A23C"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-label">今日报工数</div>
                <div class="stat-value">{{ totalRecordCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <el-icon :size="36" color="#F56C6C"><Warning /></el-icon>
              <div class="stat-info">
                <div class="stat-label">加班工时</div>
                <div class="stat-value">{{ totalOvertimeHours.toFixed(2) }} h</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="workerId" label="工人ID" width="100" />
        <el-table-column prop="workerNo" label="工号" width="120" />
        <el-table-column prop="workerName" label="姓名" width="100" />
        <el-table-column prop="totalHours" label="总工时(h)" width="120" align="center">
          <template #default="{ row }">
            <span style="font-weight: bold; color: #409EFF;">{{ (row.totalHours || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="directHours" label="直接工时(h)" width="120" align="center" />
        <el-table-column prop="auxiliaryHours" label="辅助工时(h)" width="120" align="center" />
        <el-table-column prop="overtimeHours" label="加班工时(h)" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #F56C6C;">{{ (row.overtimeHours || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="recordCount" label="报工次数" width="100" align="center" />
        <el-table-column prop="totalQty" label="完成数量" width="100" align="center" />
        <el-table-column prop="lastReportTime" label="最后报工时间" width="180" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="isWorking(row) ? 'success' : 'info'">
              {{ isWorking(row) ? '工作中' : '空闲' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="refreshWorker(row)">
              刷新缓存
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllTodayLaborHours, refreshTodayLaborCache } from '@/api/redisLabor'

const loading = ref(false)
const tableData = ref([])
const lastUpdateTime = ref('')
const autoRefresh = ref(true)
let timer = null

const onlineWorkerCount = computed(() => tableData.value.length)
const totalWorkHours = computed(() =>
  tableData.value.reduce((sum, item) => sum + (Number(item.totalHours) || 0), 0)
)
const totalRecordCount = computed(() =>
  tableData.value.reduce((sum, item) => sum + (item.recordCount || 0), 0)
)
const totalOvertimeHours = computed(() =>
  tableData.value.reduce((sum, item) => sum + (Number(item.overtimeHours) || 0), 0)
)

const isWorking = (row) => {
  if (!row.lastReportTime) return false
  const lastTime = new Date(row.lastReportTime)
  const now = new Date()
  const diff = (now - lastTime) / (1000 * 60)
  return diff < 120
}

const formatTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAllTodayLaborHours()
    const data = res.data || {}
    const list = Object.values(data).map((item, index) => ({
      ...item,
      workerId: Object.keys(data)[index]
    }))
    tableData.value = list
    lastUpdateTime.value = formatTime()
  } catch (e) {
    console.error('加载实时工时失败', e)
  } finally {
    loading.value = false
  }
}

const refreshWorker = async (row) => {
  try {
    await refreshTodayLaborCache(row.workerId)
    ElMessage.success(`已刷新工人 ${row.workerName} 的工时缓存`)
    await loadData()
  } catch (e) {
    console.error('刷新缓存失败', e)
  }
}

const startAutoRefresh = () => {
  stopAutoRefresh()
  timer = setInterval(() => {
    loadData()
  }, 30000)
}

const stopAutoRefresh = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

watch(autoRefresh, (val) => {
  if (val) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
})

onMounted(() => {
  loadData()
  if (autoRefresh.value) {
    startAutoRefresh()
  }
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}

.stat-card {
  background: linear-gradient(135deg, #fff 0%, #f5f7fa 100%);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}
</style>
