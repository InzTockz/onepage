# ============================================
# ETAPA 1: Build - Compila el proyecto con Maven
# ============================================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copiar el wrapper de Maven y el pom.xml primero (aprovecha cache de Docker)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecucion al wrapper
RUN chmod +x mvnw

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el codigo fuente
COPY src src

# Compilar el JAR sin ejecutar tests
RUN ./mvnw package -DskipTests -B

# ============================================
# ETAPA 2: Runtime - Solo el JRE y el JAR
# ============================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copiar SOLO el JAR ejecutable (excluir el plain)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
