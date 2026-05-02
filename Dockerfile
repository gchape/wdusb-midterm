FROM maven:3.9.15-eclipse-temurin-25-alpine as build
WORKDIR /webapp
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /webapp
COPY --from=build /webapp/target/*.jar webapp.jar
ENTRYPOINT ["java", "-jar", "webapp.jar"]