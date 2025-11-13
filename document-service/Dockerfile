# Etapa 1: Build (usar JDK completo basado en Debian)
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiamos archivos necesarios para caching de dependencias
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copiamos el código fuente
COPY src ./src

# Construimos el jar (sin correr tests)
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime (ligera y optimizada)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Dependencias livianas (solo si las necesitás)
RUN apk add --no-cache curl

# Puerto que expone el cliente
EXPOSE 8085

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
