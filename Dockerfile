# 改用AdoptOpenJDK的公开Java 8镜像（无需授权，全球可访问）
FROM adoptopenjdk:8-jdk-hotspot

# 创建临时目录（Spring Boot 运行需要）
VOLUME /tmp

# 将打包后的 jar 文件复制到容器中，并重命名为 app.jar（简化命令）
# 注意：这里的 jar 文件名要和你项目打包后的一致！
# 示例 jar 名：first-springboot-0.0.1-SNAPSHOT.jar（根据你的实际情况修改）
# 直接从项目根目录复制jar，无需target目录
COPY ./app.jar app.jar


# 容器启动时执行的命令（启动 Spring Boot 项目）
ENTRYPOINT ["java","-jar","/app.jar"]