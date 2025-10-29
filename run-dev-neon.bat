@echo off
echo Starting LINA Data Portal Backend (Development - Neon PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=dev-neon
pause