<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">排产与实际对比</span>
        <el-button type="primary" @click="loadData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="订单">
          <el-select v-model="queryForm.orderId" placeholder="全部订单" clearable filterable style="width: 250px;">
            <el-option
              v-for="item in orderOptions"
              :key="item.id"
              :label="`${item.orderNo} - ${item.orderName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><RefreshLeft /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="workOrderId" label="工单ID" width="100" />
        <el-table-column prop="workOrderNo" label="工单编号" width="150" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="processId" label="工序ID" width="100" />
        <el-table-column prop="processName" label="工序名称" min-width="120" />
        <el-table-column prop="planQty" label="计划数量" width="100" align="center" />
        <el-table-column prop="completedQty" label="实际完成" width="100" align="center" />
        <el-table-column prop="qtyDiff" label="数量差异" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="(row.qtyDiff || 0) < 0 ? 'danger' : (row.qtyDiff || 0) === 0 ? 'success' : 'warning'">
              {{ row.qtyDiff || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="planHours" label="计划工时(h)" width="120" align="center" />
        <el-table-column prop="actualHours" label="实际工时(h)" width="120" align="center" />
        <el-table-column prop="hoursDiff" label="工时差异" width="120" align="center">
          <template #default="{ row }">
            <span :style="{ color: (row.hoursDiff || 0) > 0 ? '#F56C6C' : '#67C23A', fontWeight: 'bold' }">
              {{ row.hoursDiff || 0 }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="efficiency" label="效率(%)" width="100" align="center">
          <template #default="{ row }">
            <el-progress
              :percentage="row.efficiency || 0"
              :color="row.efficiency >= 100 ? '#67C23A' : row.efficiency >= 80 ? '#E6A23C' : '#F56C6C'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="planStartTime" label="计划开始" width="180" />
        <el-table-column prop="planEndTime" label="计划结束" width="180" />
        <el-table-column prop="status" label="工单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 2 ? '已完成' : row.status === 1 ? '进行中' : '待开始' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { compareWorkOrder } from '@/api/summary'
import { listOrders } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const orderOptions = ref([])

const queryForm = reactive({
  orderId: null
})

const loadOrders = async () => {
  try {
    const res = await listOrders()
    orderOptions.value = res.data || []
  } catch (e) {
    console.error('加载订单列表失败', e)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (queryForm.orderId) {
      params.orderId = queryForm.orderId
    }
    const res = await compareWorkOrder(params)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.orderId = null
  loadData()
}

onMounted(() => {
  loadOrders()
  loadData()
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

.query-form {
  margin-bottom: 20px;
}
</style>
