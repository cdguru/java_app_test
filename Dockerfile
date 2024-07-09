FROM maven:3.9.8-amazoncorretto-21-al2023 as builder

WORKDIR /app
COPY ./*.xml ./
COPY ./src ./src
RUN mvn clean compile install -DskipTests=true

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/*.jar ./app.jar
RUN groupadd appgroup
RUN useradd appuser
RUN usermod -a -G appgroup appuser
USER appuser
CMD ["/bin/sh", "-c", "java -XX:MaxRAMPercentage=90.0 -jar app.jar"]