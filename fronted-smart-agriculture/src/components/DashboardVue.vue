<template>
  <div>
    <h2>Dashboard</h2>
    <el-row>
      <el-col :span="6">
        <el-card>
          <!-- 使用 v-slot:header 替代 slot="header" -->
          <template v-slot:header>
            <div class="clearfix">
              <span>Device 1 Status</span>
            </div>
          </template>
          <div>{{ deviceStatus }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import api from '../api'; // 引入 API 请求文件

export default {
  data() {
    return {
      deviceStatus: 'Loading...'
    };
  },
  created() {
    this.fetchDeviceStatus();
  },
  methods: {
    async fetchDeviceStatus() {
      try {
        const response = await api.get('http://localhost:8081/api/devices');  // 获取设备状态
        this.deviceStatus = response.data.status;
      } catch (error) {
        console.error('Failed to fetch device status:', error);
        this.deviceStatus = 'Error fetching status';
      }
    }
  }
};
</script>
