<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon :size="40" color="#409EFF"><User /></el-icon>
            <div class="stat-info">
              <div class="stat-label">工人总数</div>
              <div class="stat-value">{{ stats.workerCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon :size="40" color="#67C23A"><Tickets /></el-icon>
            <div class="stat-info">
              <div class="stat-label">进行中订单</div>
              <div class="stat-value">{{ stats.orderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon :size="40" color="#E6A23C"><List /></el-icon>
            <div class="stat-info">
              <div class="stat-label">今日报工记录</div>
              <div class="stat-value">{{ stats.todayRecordCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <el-icon :size="40" color="#F56C6C"><Timer /></el-icon>
            <div class="stat-info">
              <div class="stat-label">今日总工时</div>
              <div class="stat-value">{{ stats.todayWorkHours }} h</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>各工人工时统计（今日）</span>
          </template>
          <div ref="workerChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <span>工序工时占比</span>
          </template>
          <div ref="processChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span>近期报工趋势</span>
          </template>
          <div ref="trendChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { getTodayRealtimeSummary } from '@/api/summary'

const stats = reactive({
  workerCount: 0,
  orderCount: 0,
  todayRecordCount: 0,
  todayWorkHours: '0.00'
})

const workerChartRef = ref(null)
const processChartRef = ref(null)
const trendChartRef = ref(null)

let workerChart = null
let processChart = null
let trendChart = null

const initCharts = () => {
  if (workerChartRef.value) {
    workerChart = echarts.init(workerChartRef.value)
    workerChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['张三', '李四', '王五', '赵六', '孙七', '周八', '吴九']
      },
      yAxis: { type: 'value', name: '工时(h)' },
      series: [{
        name: '工时',
        type: 'bar',
        data: [7.5, 8.2, 6.8, 9.1, 7.3, 8.5, 6.5],
        itemStyle: { color: '#409EFF' }
      }]
    })
  }

  if (processChartRef.value) {
    processChart = echarts.init(processChartRef.value)
    processChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: '0%' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        labelLine: { show: false },
        data: [
          { value: 45, name: '组装' },
          { value: 30, name: '焊接' },
          { value: 20, name: '喷涂' },
          { value: 15, name: '包装' },
          { value: 10, name: '质检' }
        ]
      }]
    })
  }

  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['直接工时', '辅助工时'] },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: { type: 'value', name: '工时(h)' },
      series: [
        {
          name: '直接工时',
          type: 'line',
          stack: 'Total',
          data: [120, 132, 101, 134, 90, 230, 210],
          smooth: true,
          itemStyle: { color: '#67C23A' }
        },
        {
          name: '辅助工时',
          type: 'line',
          stack: 'Total',
          data: [20, 18, 25, 22, 30, 35, 28],
          smooth: true,
          itemStyle: { color: '#E6A23C' }
        }
      ]
    })
  }
}

const loadData = async () => {
  try {
    const res = await getTodayRealtimeSummary()
    if (res.data) {
      stats.workerCount = res.data.length
      const totalHours = res.data.reduce((sum, item) => sum + (Number(item.workHours) || 0), 0)
      stats.todayWorkHours = totalHours.toFixed(2)
      stats.todayRecordCount = res.data.reduce((sum, item) => sum + (item.recordCount || 0), 0)
    }
  } catch (e) {
    console.error('加载数据失败', e)
  }
}

onMounted(() => {
  loadData()
  initCharts()
  window.addEventListener('resize', () => {
    workerChart?.resize()
    processChart?.resize()
    trendChart?.resize()
  })
})
</script>

<style scoped>
.stat-card {
  background: linear-gradient(135deg, #fff 0%, #f5f7fa 100%);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
