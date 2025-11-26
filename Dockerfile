# 使用 Eclipse Temurin 8 JDK 镜像作为基础镜像
FROM eclipse-temurin:8-jdk-alpine

# 安装 Maven
RUN apk add --no-cache maven

# 设置工作目录
WORKDIR /app

# 复制所有源代码和配置文件
COPY . .

# 构建应用
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8081

# 运行应用
ENTRYPOINT ["java","-jar","target/new_iot20220217079-0.0.1-SNAPSHOT.jar"]