FROM openjdk:17-jdk-slim
MAINTAINER ravinduroshan
RUN ulimit -c unlimited
EXPOSE 8090
COPY target/*.jar integration-service-1.0.0.jar
ENTRYPOINT ["java","-jar","integration-service-1.0.0.jar"]