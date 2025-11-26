# 基础镜像：使用轻量的 Java 8 环境（适配 Spring Boot 项目）
# 替换为腾讯云维护的Java 8镜像（稳定可用）
FROM ccr.ccs.tencentyun.com/mirrors/openjdk:8u382-jdk-slim

# 创建临时目录（Spring Boot 运行需要）
VOLUME /tmp

# 将打包后的 jar 文件复制到容器中，并重命名为 app.jar（简化命令）
# 注意：这里的 jar 文件名要和你项目打包后的一致！
# 示例 jar 名：first-springboot-0.0.1-SNAPSHOT.jar（根据你的实际情况修改）
COPY target/first-springboot-0.0.1-SNAPSHOT.jar app.jar

# 容器启动时执行的命令（启动 Spring Boot 项目）
ENTRYPOINT ["java","-jar","/app.jar"]