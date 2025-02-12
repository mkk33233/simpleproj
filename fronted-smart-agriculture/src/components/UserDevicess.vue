<template>
  <div>
    <h2 v-if="username" class="welcome-text">
      <span id="welcome-text-part">Welcome, </span>
      <span id="username-text">{{ username }}</span>
    </h2>


      <!-- 添加设备按钮 -->
      <el-button @click="openAddDeviceDialog" type="primary">添加设备</el-button>
    <div>
      <h2 id="hh2">{{ devices.length === 0 ? '您还未添加设备' : '您的设备' }}</h2>
      <el-row :gutter="20">
        <!-- 动态生成设备卡片 -->
        <el-col :span="6" v-for="device in devices" :key="device.id">
          <el-card :class="device.status === 'active' ? 'active-card' : 'inactive-card'">
            <!-- 卡片头部 -->
            <template v-slot:header>
              <div class="clearfix">
                <span>{{ device.name }} {{ device.id }} </span>
              </div>
            </template>
              <h3 v-if="gxvs"  style="color: green;"> 更新数据中 </h3>
              <h4 v-if="dqvs"  style="color: green;">读取数据库数据中</h4>
            <!-- 显示设备状态 -->    
            <div v-if="yvs">
              <p>Status: {{ device.status === 'active' ? 'active' : 'inactive' }}</p>
              <!-- 显示最新检测数据 -->
              <p v-if="device.latestData && device.latestData.length > 0">
                 
                <span v-for="(data, index) in device.latestData" :key="index">
                 
                  <span v-if="data.timestamp">
                    Latest Data: <br />  {{ formatTimestamp(data.timestamp) }}<br /> 
                </span >
                 
                  <span v-if="data.status">
                    {{ data.status }} ！！
                  </span>
                  <span v-if="data.DataType">
                    {{ data.DataType }}:
                  </span>
                  <span v-if="data.value">
                    {{ data.value }} 
                  </span>
                  <span v-if="data.status || data.DataType || data.value">
                    <br />
                  </span>
                </span>
              </p>

              <!-- 显示关联的作物信息 -->
              <p >
                检测的作物Crop: {{ device.crop.name }} (Growth Cycle: {{ device.crop.growthCycle }})
              </p>
            </div>

            <!-- 切换设备状态按钮 -->
            <el-button
              @click="toggleDeviceStatus(device)"
              :type="device.status === 'active' ? 'danger' : 'success'">
              {{ device.status === 'active' ? '关闭设备' : '开启设备' }}
            </el-button>

             <el-button 
            v-if="historyData" 
              @click="handleHistoryDataClick(device.id)" 
              type="info"
              >
              查看历史数据
            </el-button>  

   
            <!-- 主动检测按钮 -->
            <el-button v-if="device.status === 'active'" @click="triggerSensorCheck(device.id)" type="primary">
              主动检测
            </el-button>
            <!-- 删除设备按钮 -->
          <el-button @click="deleteDevice(device.id)" type="danger">
                   删除设备
            </el-button>  

          </el-card>
    
        </el-col>
      </el-row>





      <div v-if="visableChart">
      <div v-for="(data, type) in groupedData" :key="type">
        <h3>{{ type }}</h3>
        <div ref="chart" :id="`chart-${type}`" style="height: 400px;"></div>
      </div>
     </div>


     




 






     <el-button v-if="visableChart" @click="visableChart=false" type="danger">
                   关闭表格
            </el-button>  






     
    </div>

    <!-- 添加设备的对话框 -->
    <el-dialog title="添加设备" v-model="addDeviceDialogVisible" width="500px">
      <el-form :model="newDevice" label-width="100px">
        <el-form-item label="设备名称">
          <el-input v-model="newDevice.name" placeholder="设备名称"></el-input>
        </el-form-item>
        <el-form-item label="设备类型">
          <el-select v-model="newDevice.type" placeholder="选择设备类型">
            <el-option label="温度" value="温度"></el-option>
            <el-option label="湿度" value="湿度"></el-option>
            <el-option label="水质" value="水质"></el-option>
            <el-option label="综合" value="综合"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作物名称">
          <el-input v-model="cr.name" placeholder="作物名称"></el-input>
        </el-form-item>
        <el-form-item label="生长周期(天)">
          <el-input v-model="cr.growthCycle" type="number"  placeholder="作物生长周期"></el-input>
        </el-form-item>
      </el-form>

      <template v-slot:footer>
        <span class="dialog-footer">
          <el-button @click="addDeviceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addDevice">确认添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import * as echarts from 'echarts'; // 引入 ECharts 库
import api from '../api'; // 引入 API 请求文件
 
 

export default {
  data() {
    return { 
      dqvs:true,
      gxvs:false,
      yvs:false,
      jcvs:false,
      groupedData: null,
       visableChart: false, // 控制图表的显示与隐藏
       historyData: [], // 存储历史数据                      
      username: '', // 存储用户名
      devices: [], // 存储设备列表
      addDeviceDialogVisible: false, // 控制添加设备对话框的显示与隐藏
      newDevice: {
        name: '',
        type: '',
        crop:'',
        status: 'inactive',
      },
      cr:{ name:'',growthCycle:''},
    };
  },
  mounted() {
    const username = this.$route.query.username || ''; // 获取传递的用户名
    this.username = username; // 更新组件中的用户名
    this.intervalId = setInterval(() => {
      this.dsupdate();
    }, 3000); // 10000ms = 10秒
    // 获取设备ID列表并加载设备信息
    this.fetchDeviceIdsByUsername(username);
  },
  methods: {

    


    handleHistoryDataClick(deviceid) {
  api.get(`http://localhost:8081/api/devices/${deviceid}/history/grouped`)
    .then(response => {
        alert ("-=-=-=-=")
        this.groupedData = response.data;
        this.renderCharts(deviceid) ;  // 延迟1秒渲染图表
         

      
    })
    .catch(() => {  
      // 可以在这里处理错误
    });
},
 




//adsadsadsasad/asdsada
// 根据数据渲染图表
renderCharts( ) {  this.visableChart = true;//放这里不会出现空的 echart
  // 在数据更新后确保 DOM 渲染完成
  
  
   
  this.$nextTick(() => {                                                  
    for (const [type, data] of Object.entries(this.groupedData)) { 
       
      const chartElement = document.getElementById(`chart-${type}`);
      
      // 确保 chartElement 存在
      if (!chartElement) {alert("asaasa");
        console.error(`无法找到 chart 元素: chart-${type}`);
        return;
      }
      
      const chartInstance = echarts.init(chartElement);
       
      const option = {
        title: {
          text: `${type} 数据`,
          left: 'center',
        },
        tooltip: {
          trigger: 'axis',
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.timestamp),
          axisLabel: {
            rotate: 45,  // 旋转标签以便显示
          },
        },
        yAxis: {
          type: 'value',
        },
            series: [{
        data: data.map(item => item.value),
        type: 'line', // 使用折线图类型
        lineStyle: {width:1,type: 'solid',  color: '#ff6347',  // 设置折线的颜色，可以是任何合法的颜色值
              },
        areaStyle: {
        color: 'rgba(255, 99, 71, 1)',  // 设置填充颜色，确保与折线颜色一致，调整透明度
          },
}],
      };

      chartInstance.setOption(option);   
    } 
  })         


  

   
}   ,  


    // 根据数据渲染图表
    // renderCharts() {


    //   this.visableChart = true;
      
      
      
      
      
      
    //   // const chartElement = this.$refs.chart;
    //   // alert(this.$refs.chart)
    //   // const chartInstance = echarts.init(chartElement);

    //   // const data = this.historyData.map(item => ({
    //   //   timestamp: item.timestamp,
    //   //   value: item.value,
    //   // }));

    //   // const option = {
    //   //   xAxis: {
    //   //     type: 'category',
    //   //     data: data.map(item => item.timestamp),  // 时间戳作为x轴
    //   //   },
    //   //   yAxis: {
    //   //     type: 'value',
    //   //   },
    //   //   series: [{
    //   //     data: data.map(item => item.value),  // 设备数据值作为y轴
    //   //     type: data.length === 1 ? 'scatter' : 'line',  // 如果只有一条数据，展示为散点图；否则为折线图
    //   //   }],
    //   // };

    //   // chartInstance.setOption(option);


      
    // },
//     renderCharts() {
      
     
     
//   for (const [type, data] of Object.entries(this.groupedData)) { 
//     const chartElement = document.getElementById(`chart-${type}`);
//     const chartInstance = echarts.init(chartElement);

//     const option = {
//       title: {
//         text: `${type} 数据`,
//         left: 'center',
//       },
//       tooltip: {
//         trigger: 'axis',
//       },
//       xAxis: {
//         type: 'category',
//         data: data.map(item => item.timestamp),
//         axisLabel: {
//           rotate: 45,  // 旋转标签以便显示
//         },
//       },
//       yAxis: {
//         type: 'value',
//       },
//       series: [{
//         data: data.map(item => item.value),
//         type: 'line',  // 对于数据类型为temperature和humidity，都使用折线图
//         smooth: true, 
//       }],
//     };

//     chartInstance.setOption(option);   
//   }
// }
// ,





//asdsadas






exit(){ 
  this.visableChart=false;  },














  // 删除设备
  async deleteDevice(deviceId) {
    try { 

     
      const token=localStorage.getItem('token')
    
      // 1. 删除 userdevice 表中的数据
      const dd=await api.get(`http://localhost:8081/api/devices/aa`,{headers: {Authorization:`mkk ${token}`}});
       alert(dd.data+"====dd.data");
       //this.fetchLatestData();  // 重新加载设备列表
      // 3. 删除设备后，重新加载设备信息
      if(dd.data=='token可用'){await api.delete(`http://localhost:8081/api/devices/devices/${deviceId}`);alert(dd.data);
        
        this.fetchDeviceIdsByUsername(this.username);  // 提示用户删除成功
        alert('设备删除成功');}  // 重新加载设备列表
     else
       {alert(dd.data+"----token错了")}

         


      
    } catch (error) {
      console.error('删除设备失败:', error);
      alert('删除设备-----------失败，请重试');
    }
  },

  // 其他方法...
 





    // 根据用户名获取设备ID列表
    async fetchDeviceIdsByUsername(username) {
      try {
        const response = await api.get(`http://localhost:8081/api/devices/username/${username}`);
        this.devices = response.data.map(device => ({
          ...device,
          latestData: device.latestData || null // 确保每个设备都有 latestData 字段
        }));

        // 在设备加载完毕后，获取每个设备的最新数据
        this.fetchLatestData(); 
      } catch (error) {
        console.error('Failed to fetch devices:', error);
      }
    },

 // 首次访问页面 获取每个设备的最新数据
async fetchLatestData() { 
  const promises = [];  // 存储每个设备的数据请求Promise

  // 循环处理每个设备的数据请求
  for (let device of this.devices) {
    if((!device.latestData == ([{ value: '更新数据中...', status: '', DataType: '', timestamp: '' }]))){this.dqvs=true; this.yvs=false;}

    const promise = new Promise((resolve, reject) => {//alert("读取数据中...111=")
      api.post(`http://localhost:8081/api/devices/${device.id}/latestDatas`)
        .then(response => {
          // 设置默认的读取数据中状态
           
          const sensorDataList = response.data;
          if (sensorDataList && sensorDataList.length > 0) {
            const sensorData = sensorDataList.slice(0, 2); // 取前两条数据
            const formattedDataList = sensorData.map(sensorData => ({
              status: sensorData.status || '',
              value: sensorData.value || '无数据',
              DataType: sensorData.DataType || '',
              timestamp: sensorData.timestamp || new Date().toLocaleString(),
            }));

            // 通过 resolve 返回处理后的数据
            resolve(formattedDataList);//alert("读取数据中...222=")
          } else {
            // 没有数据时返回默认的"暂无数据"
            resolve([{ value: '暂无数据', status: '', DataType: '', timestamp: '' }]);
          }
        })
        .catch(error => {
          console.error('Failed to trigger sensor check:', error);
          reject(error);
        });
    });
     //这种只有 resolve内的 为 并发事件 --得到结果
     // 变字段  等五秒 并发执行得到全部   结果更新
    // 将请求添加到 promises 数组中
    promises.push(promise);
  }
   
  // 等待所有设备的数据请求完成
   //所有设备采集功能完成 等待即可同时展示
   
    // 等待 3 秒钟再执行后续操作
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    this.dqvs=false; this.yvs=true;
    try {
    const results = await Promise.all(promises);
      //并发完成的事 resolve事件   改变字段 不在其中
    
    
      // 使用返回的数据更新设备的 latestData
    results.forEach((formattedDataList, index) => {
      this.updateDeviceLatestData(this.devices[index].id, formattedDataList);
    });

    // 所有设备的数据更新完成后的操作（如刷新页面UI等）
    console.log('所有设备数据更新完成！');
  } catch (error) {
    console.error('某些设备数据更新失败:', error);
    alert('某些设备数据更新失败，请重试');
  }
},



async dsupdate() {
  const promises = [];
  this.fetchDeviceIdsByUsername(this.username)  // 存储每个设备的请求Promise
  await new Promise(resolve => setTimeout(resolve, 2000)); 
  for (let device of this.devices) {
    if (device.status === 'active') {
        this.gxvs=true;
         this.yvs=false;
      // device.latestData = [{ value: '更新数据中...', status: '', DataType: '', timestamp: '' }];  // 显示更新中
      await new Promise(resolve => setTimeout(resolve, 2000)); 
                                  //  延迟3秒后开始请求 这个流程为一个promise全部都准备好了 然后一起执行这个过程
      // 使用 setTimeout 实现延迟，并用 Promise 包装每个设备的更新操作
      //等三秒，然后实现更新       结果为更新    更新字段后 来到这里 并发执行三秒 然后更新结果

      const promise = new Promise((resolve, reject) => {//alert("更新数据中...3333=")
        // 延迟3秒后开始请求
        setTimeout(async () => {//alert("更新数据中...kssksk=")
          try {
            const response = await api.post(`http://localhost:8081/api/devices/${device.id}/latestDatas`);
            const sensorDataList = response.data;
            const sensorData = sensorDataList.slice(0, 2); // 获取前两条数据
            
            // 格式化传感器数据
            const formattedDataList = sensorData.map(sensorData => ({
              status: sensorData.status || '',
              value: sensorData.value || '无数据',
              DataType: sensorData.DataType || '',
              timestamp: sensorData.timestamp || new Date().toLocaleString()
            }));
            this.gxvs=false; this.yvs=true
            // 更新设备的最新数据
            this.updateDeviceLatestData(device.id, formattedDataList);
            resolve();   //alert("更新数据中...4444=")  // 请求完成后 resolvea
          } catch (error) {
            console.error('定时更新出错了', error);
            reject(error);  // 出错时 reject
          }
        }, 3000);  // 延迟3秒执行请求
      });

      promises.push(promise);  // 将当前设备的请求加入 promises 数组
    }
  }

  // 等待所有设备的请求完成
  try {
    await Promise.all(promises);  // 同时执行所有请求
    console.log('所有设备数据更新完成！');
  } catch (error) {
    alert('某些设备更新失败，请重试');
  }
}
,
    

// 切换设备状态
    async toggleDeviceStatus(device) {
      const newStatus = device.status === 'active' ? 'inactive' : 'active';

      try {
        // 更新设备状态
        await api.put(`http://localhost:8081/api/devices/${device.id}/control`, { status: newStatus });

        // 更新前端设备状态
        device.status = newStatus;

        // 如果设备变为 active，先显示"检测中..."，然后触发检测
        if (newStatus === 'active') {
           
           await new Promise(resolve => setTimeout(resolve, 2000)); 
         
            this.triggerSensorCheck(device.id);
        
        }
      } catch (error) {
        console.error('Failed to update device status:', error);
        // 如果更新失败，回滚设备状态
        device.status = device.status === 'active' ? 'inactive' : 'active';
        alert('设备状态更新失败，请重试');
      }
    },

    // 触发传感器检测
    async triggerSensorCheck(deviceId) {
      const device = this.devices.find(d => d.id === deviceId);
      if (device) {
        device.latestData = [{ value: '检测中...', status: '', DataType: '', timestamp: '' }]; // 显示“检测中...”
      }

      try {
        const response = await api.post(`http://localhost:8081/api/devices/${deviceId}/triggerSensorCheck`);

        // 获取传感器数据
        const sensorDataList = response.data;

        // 检查数据是否有效
        if (sensorDataList && sensorDataList.length > 0) {
          const sensorData = sensorDataList.slice(0, 2); // 获取前两条数据

          // 格式化传感器数据
          const formattedDataList = sensorData.map(sensorData => ({
            status: sensorData.status || '',  
            value: sensorData.value || '无数据',
            DataType: sensorData.DataType || '',
            timestamp: sensorData.timestamp || new Date().toLocaleString()
          }));

          // 更新设备的最新数据
          this.updateDeviceLatestData(deviceId, formattedDataList);
        } else {
          console.error('没有返回有效的传感器数据');
          alert('传感器检测失败，请重试');
        }
      } catch (error) {
        console.error('Failed to trigger sensor check:', error);
        alert('传感器检测失败，请重试');
      }
    },

    // 更新设备的最新数据
    updateDeviceLatestData(deviceId, formattedDataList) {
      const device = this.devices.find(d => d.id === deviceId);
      if (device) {
        device.latestData = formattedDataList;
      }
    },

    // 格式化时间戳
    formatTimestamp(timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleString(); // 格式化时间戳为本地时间
    },

    // 打开添加设备对话框
    openAddDeviceDialog() {this.cr.name="";this.cr.growthCycle="";  
      this.addDeviceDialogVisible = true;
    },

     // 添加设备
  async addDevice() { 

    if(!(this.cr.name && this.cr.growthCycle &&this.newDevice.type&&this.newDevice.type )) { 
    alert("不能有空的") ;return;
  }



  const crop={ 
    name:this.cr.name,
    growthCycle:this.cr.growthCycle+"天",
  }
  
  try {const responsec = await api.post('http://localhost:8081/api/crops',crop);
  
   responsec.data.id;  this.newDevice.crop={name:this.cr.name,growthCycle:this.cr.growthCycle+"天"}   ;
     alert("sdsadasa=========");
   
 this.newDevice.crop=responsec.data;

   const responsed = await api.post('http://localhost:8081/api/devices', this.newDevice);
  alert("qwe");
  if (responsed.data) {

                      

                              const rs = await api.get(`http://localhost:8081/api/users/${this.username}`, this.username);
            
                                    
               const ud ={
        user: rs.data,
        device:responsed.data,
    } ;
    
     alert(ud.user.name+"namemem");  
            const responseu = await api.post('http://localhost:8081/api/devices/ud', ud);
       alert(responseu.data);
            if(responseu.data){console.log("添加成功");}
        this.devices.push(responsed.data); // 将新设备添加到列表中
   this.fetchDeviceIdsByUsername(this.username); // 重新加载设备卡片
 
  
  this.addDeviceDialogVisible = false; // 关闭对话框
  }
     } catch (error) {
         console.error('Failed to add device:', error);
     alert('添加设备失败，请重试');
    }
  }




 
  }
};
</script>
