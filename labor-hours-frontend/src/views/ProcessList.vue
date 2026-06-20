<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">工序管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增工序
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="工序名称">
          <el-input v-model="queryForm.processName" placeholder="请输入工序名称" clearable />
        </el-form-item>
        <el-form-item label="工序编号">
          <el-input v-model="queryForm.processNo" placeholder="请输入工序编号" clearable />
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
        <el-table-column prop="processNo" label="工序编号" width="150" />
        <el-table-column prop="processName" label="工序名称" min-width="150" />
        <el-table-column prop="standardHour" label="标准工时(h)" width="120" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules">
        <el-form-item label="工序编号" prop="processNo">
          <el-input v-model="formData.processNo" />
        </el-form-item>
        <el-form-item label="工序名称" prop="processName">
          <el-input v-model="formData.processName" />
        </el-form-item>
        <el-form-item label="标准工时" prop="standardHour">
          <el-input v-model="formData.standardHour" type="number" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="formData.sort" type="number" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import { listProcesses, pageProcesses, addProcess, updateProcess, deleteProcess } from '@/api/process'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryForm = reactive({
  processName: '',
  processNo: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  processNo: '',
  processName: '',
  standardHour: null,
  sort: 0,
  status: 1,
  remark: ''
})

const formRules = {
  processNo: [{ required: true, message: '请输入工序编号', trigger: 'blur' }],
  processName: [{ required: true, message: '请输入工序名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    }
    const res = await pageProcesses(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.processName = ''
  queryForm.processNo = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增工序'
  Object.assign(formData, {
    id: null,
    processNo: '',
    processName: '',
    standardHour: null,
    sort: 0,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑工序'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该工序吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteProcess(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateProcess(formData)
    ElMessage.success('更新成功')
  } else {
    await addProcess(formData)
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
