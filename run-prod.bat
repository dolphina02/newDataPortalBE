@echo off
echo Starting LINA Data Portal Backend (Production - Neon PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
pause