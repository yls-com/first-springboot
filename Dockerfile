# 使用 Eclipse Temurin 17 JDK 镜像作为基础镜像，指定具体标签以避免版本变化问题
FROM eclipse-temurin:17.0.11_9-jdk-alpine AS builder

# 安装 Maven 和其他必要工具
RUN apk add --no-cache maven curl

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件，利用 Docker 层缓存
COPY pom.xml .
COPY mvnw .
COPY --from=builder .mvn .mvn

# 添加非root用户，提高安全性
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 下载依赖但不编译代码，充分利用缓存
RUN mvn dependency:go-offline -B

# 复制所有源代码和配置文件
COPY src src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17.0.11_9-jre-alpine

# 添加非root用户，提高安全性
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 设置工作目录
WORKDIR /app

# 复制JAR文件并设置权限
COPY --from=builder --chown=appuser:appgroup /app/target/new_iot20220217079-0.0.1-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown appuser:appgroup /app/logs

# 暴露端口 - 与 application.properties 中的默认端口一致
EXPOSE 8081

# 切换到非root用户运行
USER appuser

# 设置环境变量，支持 Render 平台的环境变量注入
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/heapdump.hprof"

# 添加健康检查脚本
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# 运行应用 - 使用环境变量中的 JAVA_OPTS，并用 exec 转发信号
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]