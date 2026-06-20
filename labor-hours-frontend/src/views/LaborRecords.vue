<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">报工记录</span>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="工人ID">
          <el-input v-model="queryForm.workerId" placeholder="请输入工人ID" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="工单ID">
          <el-input v-model="queryForm.workOrderId" placeholder="请输入工单ID" clearable style="width: 150px;" />
        </el-form-item>
        <el-form-item label="工时类型">
          <el-select v-model="queryForm.laborType" placeholder="全部" clearable style="width: 150px;">
            <el-option label="直接工时" :value="1" />
            <el-option label="辅助工时" :value="2" />
          </el-select>
        </el-form-item>
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
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="recordNo" label="报工单号" width="180" />
        <el-table-column prop="workerId" label="工人ID" width="100" />
        <el-table-column prop="workOrderId" label="工单ID" width="100" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="processId" label="工序ID" width="100" />
        <el-table-column prop="laborType" label="工时类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.laborType === 1 ? 'primary' : 'success'">
              {{ row.laborType === 1 ? '直接工时' : '辅助工时' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reportType" label="报工类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.reportType === 1 ? 'warning' : 'info'">
              {{ row.reportType === 1 ? '按时长' : '按数量' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="workHours" label="工时(h)" width="100" />
        <el-table-column prop="completedQty" label="数量" width="80" />
        <el-table-column prop="unitHour" label="单位工时" width="100" />
        <el-table-column prop="isOvertime" label="加班" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isOvertime === 1 ? 'danger' : 'info'">
              {{ row.isOvertime === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '有效' : '无效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end; display: flex;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageLaborRecords, deleteLaborRecord } from '@/api/laborRecord'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref([])

const queryForm = reactive({
  workerId: '',
  workOrderId: '',
  laborType: null
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    }
    const res = await pageLaborRecords(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.workerId = ''
  queryForm.workOrderId = ''
  queryForm.laborType = null
  dateRange.value = []
  loadData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该报工记录吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteLaborRecord(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
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
