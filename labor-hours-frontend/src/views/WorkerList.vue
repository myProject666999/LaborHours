<template>
  <div class="page-container">
    <el-card shadow="never">
      <div class="page-header">
        <span class="page-title">工人管理</span>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增工人
        </el-button>
      </div>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="工人姓名">
          <el-input v-model="queryForm.workerName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="queryForm.workerNo" placeholder="请输入工号" clearable />
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
        <el-table-column prop="workerNo" label="工号" width="120" />
        <el-table-column prop="workerName" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : row.gender === 2 ? '女' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
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
        <el-form-item label="工号" prop="workerNo">
          <el-input v-model="formData.workerNo" />
        </el-form-item>
        <el-form-item label="姓名" prop="workerName">
          <el-input v-model="formData.workerName" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="formData.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="formData.phone" />
        </el-form-item>
        <el-form-item label="班组" prop="teamId">
          <el-input v-model="formData.teamId" type="number" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="formData.position" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">在职</el-radio>
            <el-radio :value="0">离职</el-radio>
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
import { listWorkers, pageWorkers, addWorker, updateWorker, deleteWorker } from '@/api/worker'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryForm = reactive({
  workerName: '',
  workerNo: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const formData = reactive({
  id: null,
  workerNo: '',
  workerName: '',
  gender: 1,
  phone: '',
  teamId: null,
  position: '',
  status: 1,
  remark: ''
})

const formRules = {
  workerNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  workerName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    }
    const res = await pageWorkers(params)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.workerName = ''
  queryForm.workerNo = ''
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增工人'
  Object.assign(formData, {
    id: null,
    workerNo: '',
    workerName: '',
    gender: 1,
    phone: '',
    teamId: null,
    position: '',
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑工人'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该工人吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteWorker(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const submitForm = async () => {
  await formRef.value?.validate()
  if (isEdit.value) {
    await updateWorker(formData)
    ElMessage.success('更新成功')
  } else {
    await addWorker(formData)
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
