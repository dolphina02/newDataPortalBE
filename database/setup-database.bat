@echo off
echo ============================================
echo LINA Data Portal Database Setup
echo ============================================

echo.
echo Connecting to Neon PostgreSQL database...
echo Database: neondb
echo Host: ep-square-shadow-a174zj2p-pooler.ap-southeast-1.aws.neon.tech
echo.

echo Running database initialization script...
psql "%DATABASE_CONNECTION_STRING%" -f init-database.sql

echo.
echo ============================================
echo Database setup completed!
echo ============================================
echo.
echo You can now run the Spring Boot application:
echo   - For H2 development: run-dev.bat
echo   - For Neon development: run-dev-neon.bat  
echo   - For production: run-prod.bat
echo.
pause