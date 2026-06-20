<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="page-title">工人报工</span>
        </div>
      </template>

      <el-form
        :model="formData"
        label-width="120px"
        ref="formRef"
        :rules="formRules"
        style="max-width: 700px; margin: 0 auto;"
      >
        <el-form-item label="选择工人" prop="workerId">
          <el-select
            v-model="formData.workerId"
            placeholder="请选择工人"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in workerOptions"
              :key="item.id"
              :label="`${item.workerNo} - ${item.workerName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="选择工单" prop="workOrderId">
          <el-select
            v-model="formData.workOrderId"
            placeholder="请选择工单"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="item in workOrderOptions"
              :key="item.id"
              :label="item.workOrderNo"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="工时类型" prop="laborType">
          <el-radio-group v-model="formData.laborType">
            <el-radio :value="1">直接工时</el-radio>
            <el-radio :value="2">辅助工时</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="报工类型" prop="reportType">
          <el-radio-group v-model="formData.reportType">
            <el-radio :value="1">按时长报工</el-radio>
            <el-radio :value="2">按数量报工</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="工作时长(h)" prop="workHours">
          <el-input-number
            v-model="formData.workHours"
            :min="0"
            :step="0.5"
            :precision="2"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="完成数量" prop="completedQty">
          <el-input-number
            v-model="formData.completedQty"
            :min="0"
            :step="1"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="是否加班" prop="isOvertime">
          <el-radio-group v-model="formData.isOvertime">
            <el-radio :value="0">否</el-radio>
            <el-radio :value="1">是</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitReport" :loading="submitting" style="width: 150px;">
            <el-icon><Check /></el-icon>提交报工
          </el-button>
          <el-button @click="resetForm" style="width: 120px;">
            <el-icon><RefreshLeft /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listWorkers } from '@/api/worker'
import { listWorkOrders } from '@/api/workorder'
import { reportLabor } from '@/api/laborRecord'

const formRef = ref(null)
const submitting = ref(false)
const workerOptions = ref([])
const workOrderOptions = ref([])

const formData = reactive({
  workerId: null,
  workOrderId: null,
  laborType: 1,
  reportType: 1,
  startTime: '',
  endTime: '',
  workHours: null,
  completedQty: 0,
  isOvertime: 0,
  remark: ''
})

const formRules = {
  workerId: [{ required: true, message: '请选择工人', trigger: 'change' }],
  workOrderId: [{ required: true, message: '请选择工单', trigger: 'change' }],
  laborType: [{ required: true, message: '请选择工时类型', trigger: 'change' }],
  reportType: [{ required: true, message: '请选择报工类型', trigger: 'change' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const loadWorkers = async () => {
  try {
    const res = await listWorkers()
    workerOptions.value = res.data || []
  } catch (e) {
    console.error('加载工人列表失败', e)
  }
}

const loadWorkOrders = async () => {
  try {
    const res = await listWorkOrders()
    workOrderOptions.value = res.data || []
  } catch (e) {
    console.error('加载工单列表失败', e)
  }
}

const submitReport = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    const payload = {
      ...formData
    }
    await reportLabor(payload)
    ElMessage.success('报工成功！')
    resetForm()
  } catch (e) {
    console.error('报工失败', e)
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(formData, {
    workerId: null,
    workOrderId: null,
    laborType: 1,
    reportType: 1,
    startTime: '',
    endTime: '',
    workHours: null,
    completedQty: 0,
    isOvertime: 0,
    remark: ''
  })
}

onMounted(() => {
  loadWorkers()
  loadWorkOrders()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
}
</style>
