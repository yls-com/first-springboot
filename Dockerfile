# 使用 Eclipse Temurin 17 JDK 镜像作为基础镜像
FROM eclipse-temurin:17-jdk-alpine AS builder

# 安装 Maven
RUN apk add --no-cache maven

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件，利用 Docker 层缓存
COPY pom.xml .
COPY mvnw .

# 下载依赖但不编译代码，充分利用缓存
RUN mvn dependency:go-offline -B

# 复制所有源代码和配置文件
COPY src src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/new_iot20220217079-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口 - 与 application.properties 中的默认端口一致
EXPOSE 8081

# 设置环境变量，支持 Render 平台的环境变量注入
ENV SPRING_PROFILES_ACTIVE=production
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 运行应用 - 使用环境变量中的 JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]