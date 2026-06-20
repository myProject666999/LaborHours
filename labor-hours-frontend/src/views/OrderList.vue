<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">订单管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增订单
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="订单编号">
          <el-input v-model="queryForm.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="订单名称">
          <el-input v-model="queryForm.orderName" placeholder="请输入订单名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 150px;">
            <el-option label="未开始" :value="0" />
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
        <el-table-column prop="orderNo" label="订单编号" width="150" />
        <el-table-column prop="orderName" label="订单名称" min-width="150" />
        <el-table-column prop="productName" label="产品名称" min-width="120" />
        <el-table-column prop="productSpec" label="规格型号" min-width="120" />
        <el-table-column prop="planQty" label="计划数量" width="100" />
        <el-table-column prop="orderDate" label="订单日期" width="120" />
        <el-table-column prop="planStartDate" label="计划开始" width="120" />
        <el-table-column prop="planEndDate" label="计划结束" width="120" />
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="row.priority === 1 ? 'danger' : row.priority === 2 ? 'warning' : 'info'">
              {{ row.priority === 1 ? '高' : row.priority === 2 ? '中' : '低' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 2 ? 'success' : row.status === 1 ? 'warning' : 'info'">
              {{ row.status === 2 ? '已完成' : row.status === 1 ? '进行中' : '未开始' }}
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
      <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules">
        <el-form-item label="订单编号" prop="orderNo">
          <el-input v-model="formData.orderNo" />
        </el-form-item>
        <el-form-item label="订单名称" prop="orderName">
          <el-input v-model="formData.orderName" />
        </el-form-item>
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="formData.productName" />
        </el-form-item>
        <el-form-item label="规格型号" prop="productSpec">
          <el-input v-model="formData.productSpec" />
        </el-form-item>
        <el-form-item label="计划数量" prop="planQty">
          <el-input v-model="formData.planQty" type="number" />
        </el-form-item>
        <el-form-item label="订单日期" prop="orderDate">
          <el-date-picker v-model="formData.orderDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="计划开始" prop="planStartDate">
          <el-date-picker v-model="formData.planStartDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="计划结束" prop="planEndDate">
          <el-date-picker v-model="formData.planEndDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="formData.priority">
            <el-radio :value="1">高</el-radio>
            <el-radio :value="2">中</el-radio>
            <el-radio :value="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">未开始</el-radio>
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
import { listOrders, pageOrders, addOrder, updateOrder, deleteOrder } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryForm = reactive({
  orderNo: '',
  orderName: '',
  status: null
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  orderNo: '',
  orderName: '',
  productName: '',
  productSpec: '',
  planQty: null,
  orderDate: '',
  planStartDate: '',
  planEndDate: '',
  priority: 2,
  status: 0,
  remark: ''
})

const formRules = {
  orderNo: [{ required: true, message: '请输入订单编号', trigger: 'blur' }],
  orderName: [{ required: true, message: '请输入订单名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    }
    const res = await pageOrders(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.orderNo = ''
  queryForm.orderName = ''
  queryForm.status = null
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增订单'
  Object.assign(formData, {
    id: null,
    orderNo: '',
    orderName: '',
    productName: '',
    productSpec: '',
    planQty: null,
    orderDate: '',
    planStartDate: '',
    planEndDate: '',
    priority: 2,
    status: 0,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑订单'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateOrder(formData)
    ElMessage.success('更新成功')
  } else {
    await addOrder(formData)
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
