# Version para Windows PowerShell. Uso: .\build-all.ps1
$ErrorActionPreference = "Stop"

foreach ($servicio in @("validation-service", "question-service", "lifecycle-service")) {
    Write-Host "=== Compilando $servicio ==="
    Push-Location $servicio
    mvn -q clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { Pop-Location; throw "Fallo la compilacion de $servicio" }
    Pop-Location
}

Write-Host "=== Levantando el ecosistema ==="
docker compose up -d --build
docker ps
