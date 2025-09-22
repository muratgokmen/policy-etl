@echo off
echo Policy ETL - Complete Development Environment Startup
echo ====================================================

echo [1/3] Starting PostgreSQL Database...
cd docker
call start-dev.bat

echo.
echo [2/3] Waiting for database to be ready...
timeout /t 10 > nul

echo.
echo [3/3] Starting Spring Boot Application...
cd ..
echo You can now run: mvn spring-boot:run
echo.
echo Available URLs after Spring Boot starts:
echo - Application: http://localhost:8080
echo - PDF Upload: http://localhost:8080/pdf-upload.html
echo - Batch Monitor: http://localhost:8080/batch-monitor.html
echo - pgAdmin: http://localhost:8080:8080
echo.
echo To start the application, run: mvn spring-boot:run
pause
