# IoT系统Docker部署说明

## 项目简介

这是一个基于Spring Boot的物联网(IoT)管理系统，包含设备管理、传感器数据收集等功能。

## 针对 Render 部署的性能优化

为了提升在 Render 平台上的部署和运行效率，我们进行了以下优化：

1. 使用多阶段 Docker 构建，减少镜像大小并加快构建速度
2. 添加了 .dockerignore 文件，避免不必要的文件进入 Docker 镜像
3. 优化了应用配置，更好地适配 Render 环境
4. 增加了性能调优配置

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

## 在 Render 平台上部署

要在 Render 平台上部署，请按照以下步骤操作：

1. Fork 此仓库到你的 GitHub 账户
2. 登录 Render 控制台 (https://dashboard.render.com)
3. 点击 "New+" -> "Web Service"
4. 连接到你的 GitHub 仓库
5. 配置以下选项：
   - Name: 任意名称
   - Region: 选择靠近你的地区
   - Branch: main
   - Root Directory: 保持为空
   - Environment: Docker
   - Plan: Free 或其他计划
6. 添加环境变量（可选）：
   - SPRING_DATASOURCE_URL: 你的数据库连接字符串
   - SPRING_DATASOURCE_USERNAME: 数据库用户名
   - SPRING_DATASOURCE_PASSWORD: 数据库密码
7. 点击 "Create Web Service"

## 环境变量说明

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| PORT | 8081 | Render 应用端口 |
| SPRING_DATASOURCE_URL | jdbc:mysql://localhost:3306/new_iot?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true | MySQL数据库连接地址 |
| SPRING_DATASOURCE_USERNAME | root | MySQL用户名 |
| SPRING_DATASOURCE_PASSWORD | 123456 | MySQL密码 |
| SPRING_MAIL_USERNAME | 1613302001@qq.com | 邮箱用户名 |
| SPRING_MAIL_PASSWORD | nxcxerihyqqbdahd | 邮箱密码 |
| SPRING_DATA_MONGODB_URI | mongodb://localhost:27017/home_devices | MongoDB连接地址 |

## 性能优化措施

1. 数据库连接池优化
2. JVM 内存设置优化
3. Docker 构建优化

## 访问应用

应用启动后可通过 http://localhost:8081 访问。