# IoT系统Docker部署说明

## 项目简介

这是一个基于Spring Boot的物联网(IoT)管理系统，包含设备管理、传感器数据收集等功能。

## Docker部署方式

### 方式一：使用Docker Compose（推荐）

```bash
# 构建并启动所有服务（包括应用、MySQL和MongoDB）
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止所有服务
docker-compose down
```

### 方式二：单独构建和运行应用容器

```bash
# 构建Docker镜像
docker build -t iot-app .

# 运行容器（需要先启动MySQL和MongoDB）
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/new_iot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=123456 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/home_devices \
  iot-app
```

## 环境变量说明

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| SPRING_DATASOURCE_URL | jdbc:mysql://localhost:3306/new_iot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true | MySQL数据库连接地址 |
| SPRING_DATASOURCE_USERNAME | root | MySQL用户名 |
| SPRING_DATASOURCE_PASSWORD | 123456 | MySQL密码 |
| SPRING_MAIL_USERNAME | 1613302001@qq.com | 邮箱用户名 |
| SPRING_MAIL_PASSWORD | nxcxerihyqqbdahd | 邮箱密码 |
| SPRING_DATA_MONGODB_URI | mongodb://localhost:27017/home_devices | MongoDB连接地址 |

## 访问应用

应用启动后可通过 http://localhost:8081 访问。