# Imagen base con Java 17 (Eclipse Temurin, reemplazo oficial de openjdk)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar el .jar generado con: mvn clean package -DskipTests
COPY target/*.jar app.jar

# Puerto del lifecycle-service
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]