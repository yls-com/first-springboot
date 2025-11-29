# IoT系统Docker部署说明

## 项目简介

这是一个基于Spring Boot的物联网(IoT)管理系统，包含设备管理、传感器数据收集等功能。

## 针对 Render 部署的性能优化

为了提升在 Render 平台上的部署和运行效率，我们进行了以下优化：

1. 使用多阶段 Docker 构建，减少镜像大小并加快构建速度
2. 添加了 .dockerignore 文件，避免不必要的文件进入 Docker 镜像
3. 优化了应用配置，更好地适配 Render 环境
4. 增加了性能调优配置

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

# IoT 系统 - 部署与本地测试说明

本项目是一个基于 Spring Boot 的物联网管理系统，包含设备管理、传感器数据采集、用户与邮件通知等功能。下面给出本地通过 Docker 运行与在 Render 部署的操作步骤与注意事项。

## 概要变更
- 已移除源码中的明文凭据，改为通过环境变量注入（见 `.env.example`）。
- `Dockerfile` 使用多阶段构建，入口命令已优化为 `exec` 形式以便正确转发信号并优雅关闭。

## 使用 `.env.example`
仓库新增了 `.env.example`，请在本地复制为 `.env` 并填写真实值：

PowerShell:

```powershell
copy .env.example .env
# 然后用编辑器填写 .env 中的真实凭据
```

.env 文件已被列入 `.gitignore`，请不要将它提交到远程仓库。

## 本地构建与运行（PowerShell 示例）

1) 使用 Maven Wrapper 打包应用：

```powershell
.\mvnw.cmd clean package -DskipTests
```

2) 使用 Docker 构建镜像：

```powershell
docker build -t new_iot_app .
```

3) 使用 `docker-compose`（推荐，包含 MySQL 与 MongoDB）：

```powershell
# 在项目根目录创建 .env（可从 .env.example 复制）

# 查看日志
1. 数据库连接池优化
# 停止并移除容器
2. JVM 内存设置优化
```

4) 仅运行应用容器（需外部 DB）：

```powershell
# 示例：运行应用并在本地 8081 端口暴露
3. Docker 构建优化
```

## 验证打包产物与 Dockerfile 匹配

- `pom.xml` 的 artifactId/version 对应生成的 Jar 名称为 `new_iot20220217079-0.0.1-SNAPSHOT.jar`，`Dockerfile` 在构建阶段会在 `/app/target/` 目录下生成该 Jar，并在最终鏡像中复制为 `app.jar`，匹配正常。
- 仓库根目录含有历史打包产物 `new_iot20220217079-0.0.1-SNAPSHOT.jar`（用于快速本地运行），`.dockerignore` 已配置为忽略 `target/` 但允许 `target/*.jar` 与根目录 jar，避免构建上下文过大。

如果你想在构建前确认 Jar 是否存在（PowerShell）：

```powershell
# 检查 target 中的 jar
Get-ChildItem -Path target -Filter "*.jar" -Recurse
# 或检查根目录 jar
Get-ChildItem -Path . -Filter "*SNAPSHOT.jar"
```

## 在 Render 上部署（简明步骤）

1. 登录 Render 控制台并创建新的 Web Service（New -> Web Service）。
2. 选择连接到你的 Git 仓库并选中本项目分支。Environment 选择 `Docker`（Render 会使用仓库内的 `Dockerfile` 构建镜像）。
3. 在 Environment Variables（或 Secret）里添加必要的变量（不要把敏感值写入仓库）：
    - `SPRING_DATASOURCE_URL`（MySQL JDBC URL）
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD`
    - `SPRING_DATA_MONGODB_URI`
    - `SPRING_MAIL_USERNAME`
    - `SPRING_MAIL_PASSWORD`
    - `HUAWEI_IOT_DEVICE_SECRET`
    - 可选：`JAVA_OPTS`（例如 `-Xmx512m -Xms256m`）
4. Render 会在构建时运行 Dockerfile 的构建阶段。如果需要自动部署（每次 push），开启 `Auto Deploy`。
5. 推荐在 Render 的 Health Check 中配置一个轻量的健康检查端点，例如 `/actuator/health` 或仓库中的 `/findAllDevices`（若已有），以便平台能判定应用是否正常运行。

## 建议与注意事项

- 不要在源码中保留明文密码或 Secret；使用 `.env`（本地）或 Render Secret（生产）管理敏感信息。
- 若你想在 CI 里先构建 Jar 并推镜像到 Registry（例如 Docker Hub），可以把 Render 配置为从镜像拉取，而不是在 Render 上构建。这样可提高构建稳定性与速度。

## 常见命令（汇总）

PowerShell:

```powershell
# 打包
.\mvnw.cmd clean package -DskipTests
# 构建镜像

# 启动（compose）
## 访问应用
# 查看容器日志

```

---

如果你希望我现在在当前环境里实际运行一次 `docker build` / `docker run` 来验证镜像，我可以帮你执行并返回构建日志（执行前我会再征得许可）。

应用启动后可通过 http://localhost:8081 访问。