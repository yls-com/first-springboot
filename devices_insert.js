// MongoDB 插入设备数据语句
// 使用 insertMany 插入10个以上的设备数据

db.devices.insertMany([
  {
    device_id: 1,
    name: "智能灯泡",
    type: "灯泡",
    status: "在线",
    brightness: 80,
    room: "客厅",
    install_time: new Date("2023-01-15T10:30:00Z")
  },
  {
    device_id: 2,
    name: "温湿度传感器",
    type: "传感器",
    status: "在线",
    brightness: 0,
    room: "卧室",
    install_time: new Date("2023-02-20T14:45:00Z")
  },
  {
    device_id: 3,
    name: "智能插座",
    type: "插座",
    status: "离线",
    brightness: 0,
    room: "厨房",
    install_time: new Date("2023-03-10T09:15:00Z")
  },
  {
    device_id: 4,
    name: "智能门锁",
    type: "门锁",
    status: "在线",
    brightness: 0,
    room: "大门",
    install_time: new Date("2023-04-05T16:20:00Z")
  },
  {
    device_id: 5,
    name: "空调控制器",
    type: "控制器",
    status: "在线",
    brightness: 0,
    room: "客厅",
    install_time: new Date("2023-05-12T11:30:00Z")
  },
  {
    device_id: 6,
    name: "窗帘电机",
    type: "电机",
    status: "离线",
    brightness: 0,
    room: "卧室",
    install_time: new Date("2023-06-18T08:45:00Z")
  },
  {
    device_id: 7,
    name: "空气质量检测仪",
    type: "检测仪",
    status: "在线",
    brightness: 0,
    room: "书房",
    install_time: new Date("2023-07-22T13:10:00Z")
  },
  {
    device_id: 8,
    name: "智能电视",
    type: "电视",
    status: "在线",
    brightness: 90,
    room: "客厅",
    install_time: new Date("2023-08-30T17:25:00Z")
  },
  {
    device_id: 9,
    name: "音响系统",
    type: "音响",
    status: "离线",
    brightness: 0,
    room: "影音室",
    install_time: new Date("2023-09-14T12:00:00Z")
  },
  {
    device_id: 10,
    name: "智能冰箱",
    type: "冰箱",
    status: "在线",
    brightness: 70,
    room: "厨房",
    install_time: new Date("2023-10-25T15:40:00Z")
  },
  {
    device_id: 11,
    name: "洗衣机",
    type: "家电",
    status: "在线",
    brightness: 0,
    room: "洗衣房",
    install_time: new Date("2023-11-05T10:15:00Z")
  },
  {
    device_id: 12,
    name: "智能摄像头",
    type: "摄像头",
    status: "在线",
    brightness: 0,
    room: "后院",
    install_time: new Date("2023-12-08T18:30:00Z")
  }
])