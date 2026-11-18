# Imagen base con Java 17 (Eclipse Temurin, reemplazo oficial de openjdk)
FROM eclipse-temurin:17-jre

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el .jar generado con: mvn clean package -DskipTests
COPY target/*.jar app.jar

# Puerto del question-service
EXPOSE 8083

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]