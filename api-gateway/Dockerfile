# Etapa 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos archivos de Maven/Gradle para caching de dependencias
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copiamos el resto del código
COPY src ./src

# Construimos el jar
RUN ./mvnw clean package -DskipTests

RUN apk add --no-cache curl

# Etapa 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos el jar construido desde la etapa build
COPY --from=build /app/target/*.jar app.jar

RUN apk add --no-cache curl

# Exponemos el puerto (ejemplo Eureka 8761)
EXPOSE 8000

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
