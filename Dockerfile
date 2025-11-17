FROM maven:3.9.11-amazoncorretto-21-al2023 as builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-al2023
WORKDIR /app

RUN yum update -y && yum install -y shadow-utils && yum clean all

COPY --from=builder ./build/target/*.jar ./enquete-platform.jar

ENV TZ='UTC'

RUN groupadd -r spring && useradd -r -g spring spring
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "enquete-platform.jar"]