<template>
 <h2 v-if="username">Welcome, {{ username }}!</h2>

 
    <div>
      
      <h2>Dashboard</h2>
      <el-row :gutter="20">
        <!-- 动态生成设备卡片 -->
        <el-col :span="6" v-for="device in devices" :key="device.id">
          <el-card :class="device.status === 'active' ? 'active-card' : 'inactive-card'">
            <!-- 卡片头部 -->
            <template v-slot:header>
              <div class="clearfix">
                <span>{{ device.name }} {{ device.id }}        </span>
              </div>
            </template>
  
            <!-- 显示设备状态 -->
            <div>
              <p>Status: {{ device.status }}</p>
              <!-- 显示关联的作物信息 -->
              <p v-if="device.crop">
                Crop: {{ device.crop.name }} (Growth Cycle: {{ device.crop.growthCycle }})
              </p>
            </div>
  
            <!-- 切换设备状态按钮 -->
            <el-button
              @click="toggleDeviceStatus(device)"
              :type="device.status === 'active' ? 'danger' : 'success'">
              {{ device.status === 'active' ? '关闭设备' : '开启设备' }}
            </el-button>
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
        
           username: '',  // 存储用户名
            devices: []  // 存储设备列表
            };
    }, 
    mounted() {
    const username = this.$route.query.username || ''; // 获取传递的用户名
    alert(username+"usernamemememe");
    console.log(username);  
    
     this.username = username; // 更新组件中的用户名
    
    this.fetchDeviceStatus();
  },


   
    methods: {
      // 获取设备状态列表
      async fetchDeviceStatus() {
        try {
          const response = await api.get('http://localhost:8081/api/devices');  // 获取所有设备数据
          this.devices = response.data;  // 假设返回的是一个设备数组，包含 crop 信息
        } catch (error) {
          console.error('Failed to fetch devices:', error);
        }
      },
  
      // 切换设备状态
      async toggleDeviceStatus(device) {
        const newStatus = device.status === 'active' ? 'inactive' : 'active';  // 切换状态
        
        try {
          // 更新设备状态（假设后端提供了相应的接口来更新设备状态）
          await api.put(`http://localhost:8081/api/devices/${device.id}/control`, { status: newStatus });
          
          // 更新前端设备状态
          device.status = newStatus;
  
          // 如果设备变为 active，触发传感器检测
          if (newStatus === 'active') {
            await this.triggerSensorCheck(device.id);
          }
          
        } catch (error) {
          console.error('Failed to update device status:', error);
        }
      },
      
      // 触发传感器检测
      async triggerSensorCheck(deviceId) {
        try {
          // 假设后端有接口进行传感器检测
          await api.post(`http://localhost:8081/api/devices/${deviceId}/triggerSensorCheck`);
          console.log(`Sensor check triggered for device ${deviceId}`);
        } catch (error) {
          console.error('Failed to trigger sensor check:', error);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  /* 添加设备状态的样式 */
  .active-card {
    background-color: #e5f6e5;
  }
  
  .inactive-card {
    background-color: #f8d7da;
  }
  </style>
  