#!/usr/bin/env bash
# Compila los 3 microservicios (genera target/*.jar) y levanta todo con Docker Compose.
# Uso: ./build-all.sh
set -e

for servicio in validation-service question-service lifecycle-service; do
  echo "=== Compilando $servicio ==="
  (cd "$servicio" && mvn -q clean package -DskipTests)
done

echo "=== Levantando el ecosistema ==="
docker compose up -d --build
docker ps
