# =========================
# Etapa de construcción
# =========================
FROM maven:3.9-eclipse-temurin-21 AS builder

ARG NAME_APP=sistema-chocotejas

WORKDIR /app

# Copiamos POM principal y descargamos dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente del proyecto monolito
COPY src ./src

# Compilamos
RUN mvn clean package -DskipTests

# =========================
# Etapa de ejecución
# =========================
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -g 1001 -S appgroup && \
    adduser -S -u 10001 -G appgroup appuser

USER appuser

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]