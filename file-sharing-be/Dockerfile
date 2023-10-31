FROM maven:3.8.1-openjdk-11-slim AS build

COPY src /home/app/src
COPY pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
EXPOSE 5555
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]