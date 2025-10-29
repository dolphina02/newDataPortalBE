@echo off
echo Starting LINA Data Portal Backend (Development - H2 Database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
pause