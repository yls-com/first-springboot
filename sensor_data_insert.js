// MongoDB 插入传感器数据语句
// 包含不同时间(2020-2025年)和不同设备ID(从1开始)的传感器数据

db.sensor_data.insertMany([
  {
    _id: ObjectId(),
    device_id: "1",
    temperature: 22.5,
    humidity: 45.3,
    ledStatus: true,
    time: new Date("2020-03-15T08:30:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "2",
    temperature: 24.1,
    humidity: 50.7,
    ledStatus: false,
    time: new Date("2020-06-22T14:45:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "3",
    temperature: 19.8,
    humidity: 55.2,
    ledStatus: true,
    time: new Date("2021-01-10T10:15:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "4",
    temperature: 26.3,
    humidity: 40.8,
    ledStatus: false,
    time: new Date("2021-08-05T16:20:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "5",
    temperature: 21.7,
    humidity: 48.9,
    ledStatus: true,
    time: new Date("2022-02-12T11:30:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "6",
    temperature: 23.4,
    humidity: 52.1,
    ledStatus: false,
    time: new Date("2022-05-18T09:45:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "7",
    temperature: 20.2,
    humidity: 58.6,
    ledStatus: true,
    time: new Date("2022-11-22T13:10:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "8",
    temperature: 25.6,
    humidity: 38.4,
    ledStatus: false,
    time: new Date("2023-04-30T17:25:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "9",
    temperature: 18.9,
    humidity: 62.3,
    ledStatus: true,
    time: new Date("2023-09-14T12:00:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "10",
    temperature: 27.1,
    humidity: 35.7,
    ledStatus: false,
    time: new Date("2024-01-25T15:40:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "11",
    temperature: 22.8,
    humidity: 47.5,
    ledStatus: true,
    time: new Date("2024-07-05T10:15:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "12",
    temperature: 24.9,
    humidity: 49.8,
    ledStatus: false,
    time: new Date("2025-03-08T18:30:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "13",
    temperature: 19.5,
    humidity: 56.4,
    ledStatus: true,
    time: new Date("2025-09-12T07:20:00Z")
  },
  {
    _id: ObjectId(),
    device_id: "14",
    temperature: 26.7,
    humidity: 39.2,
    ledStatus: false,
    time: new Date("2025-12-20T20:10:00Z")
  }
])