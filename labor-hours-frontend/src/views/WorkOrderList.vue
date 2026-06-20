<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">工单管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增工单
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="工单编号">
          <el-input v-model="queryForm.workOrderNo" placeholder="请输入工单编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 150px;">
            <el-option label="待开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
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
        <el-table-column prop="workOrderNo" label="工单编号" width="150" />
        <el-table-column prop="orderId" label="订单ID" width="100" />
        <el-table-column prop="processId" label="工序ID" width="100" />
        <el-table-column prop="planQty" label="计划数量" width="100" />
        <el-table-column prop="completedQty" label="已完成" width="100" />
        <el-table-column prop="standardHour" label="标准工时" width="100" />
        <el-table-column prop="planStartTime" label="计划开始" width="180" />
        <el-table-column prop="planEndTime" label="计划结束" width="180" />
        <el-table-column prop="assignWorkerId" label="指定工人" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 2 ? '已完成' : row.status === 1 ? '进行中' : '待开始' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules">
        <el-form-item label="工单编号" prop="workOrderNo">
          <el-input v-model="formData.workOrderNo" />
        </el-form-item>
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="formData.orderId" type="number" />
        </el-form-item>
        <el-form-item label="工序ID" prop="processId">
          <el-input v-model="formData.processId" type="number" />
        </el-form-item>
        <el-form-item label="计划数量" prop="planQty">
          <el-input v-model="formData.planQty" type="number" />
        </el-form-item>
        <el-form-item label="已完成" prop="completedQty">
          <el-input v-model="formData.completedQty" type="number" />
        </el-form-item>
        <el-form-item label="标准工时" prop="standardHour">
          <el-input v-model="formData.standardHour" type="number" />
        </el-form-item>
        <el-form-item label="计划开始" prop="planStartTime">
          <el-date-picker v-model="formData.planStartTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="计划结束" prop="planEndTime">
          <el-date-picker v-model="formData.planEndTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="指定工人ID" prop="assignWorkerId">
          <el-input v-model="formData.assignWorkerId" type="number" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">待开始</el-radio>
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="2">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWorkOrders, pageWorkOrders, addWorkOrder, updateWorkOrder, deleteWorkOrder } from '@/api/workorder'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryForm = reactive({
  workOrderNo: '',
  status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  workOrderNo: '',
  orderId: null,
  processId: null,
  planQty: null,
  completedQty: 0,
  standardHour: null,
  planStartTime: '',
  planEndTime: '',
  assignWorkerId: null,
  status: 0,
  remark: ''
})

const formRules = {
  workOrderNo: [{ required: true, message: '请输入工单编号', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    }
    const res = await pageWorkOrders(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.workOrderNo = ''
  queryForm.status = null
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增工单'
  Object.assign(formData, {
    id: null,
    workOrderNo: '',
    orderId: null,
    processId: null,
    planQty: null,
    completedQty: 0,
    standardHour: null,
    planStartTime: '',
    planEndTime: '',
    assignWorkerId: null,
    status: 0,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑工单'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该工单吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteWorkOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateWorkOrder(formData)
    ElMessage.success('更新成功')
  } else {
    await addWorkOrder(formData)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
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
