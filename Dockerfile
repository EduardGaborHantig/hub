FROM openjdk:17-jdk-slim
LABEL maintainer="h.eduardgabor@gmail.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} hubapp.jar

ENTRYPOINT ["java","-jar","/hubapp.jar"]