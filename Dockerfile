# 使用 Eclipse Temurin 17 JDK 镜像作为基础镜像，替代已废弃的 openjdk:17-jdk-alpine
FROM eclipse-temurin:17-jdk-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件，利用 Docker 层缓存
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# 添加非root用户，提高安全性
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# 下载依赖但不编译代码，充分利用缓存
RUN ./mvnw dependency:go-offline -B

# 复制所有源代码和配置文件
COPY src src

# 构建应用
RUN ./mvnw clean package -DskipTests

# 运行阶段
# 使用 Eclipse Temurin 17 JRE 镜像作为运行时基础镜像，替代已废弃的 openjdk:17-jre-slim
FROM eclipse-temurin:17-jre-alpine

# 添加非root用户，提高安全性
RUN groupadd --system --gid 1001 appgroup && \
    useradd --system --uid 1001 --gid 1001 appuser

# 设置工作目录
WORKDIR /app

# 复制应用 JAR 文件（需要先在本地构建）
COPY --from=builder /app/target/new_iot20220217079-0.0.1-SNAPSHOT.jar app.jar

# 创建日志目录并设置权限
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# 暴露端口 - 与 application.properties 中的默认端口一致
EXPOSE 8081

# 切换到非root用户运行
USER appuser

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 添加健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

# 运行应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]