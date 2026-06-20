<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">按订单汇总</span>
        <el-button type="primary" @click="loadData">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
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
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="orderName" label="订单名称" min-width="150" />
        <el-table-column prop="productName" label="产品名称" min-width="120" />
        <el-table-column prop="planQty" label="计划数量" width="100" />
        <el-table-column prop="totalHours" label="总工时(h)" width="120">
          <template #default="{ row }">
            <span style="font-weight: bold; color: #409EFF;">{{ row.totalHours || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="directHours" label="直接工时(h)" width="120" />
        <el-table-column prop="auxiliaryHours" label="辅助工时(h)" width="120" />
        <el-table-column prop="completedQty" label="完成数量" width="100" />
        <el-table-column prop="workerCount" label="投入人数" width="100" />
        <el-table-column prop="recordCount" label="报工次数" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { summaryByOrder } from '@/api/summary'

const loading = ref(false)
const tableData = ref([])
const dateRange = ref([])

const queryForm = reactive({
  startDate: '',
  endDate: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await summaryByOrder(params)
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  dateRange.value = []
  queryForm.startDate = ''
  queryForm.endDate = ''
  loadData()
}

onMounted(() => {
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
