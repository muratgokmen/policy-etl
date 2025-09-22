@echo off
echo Policy ETL - PostgreSQL Development Environment
echo =============================================

echo Starting PostgreSQL...
docker-compose up -d postgres

echo Waiting for PostgreSQL to be ready...
timeout /t 5 > nul

echo PostgreSQL is ready!
echo.
echo Database Connection Info:
echo - Host: localhost
echo - Port: 5432  
echo - Database: policy_etl_db
echo - Username: postgres
echo - Password: postgres
echo.
echo Starting pgAdmin (optional)...
docker-compose up -d pgadmin

echo.
echo pgAdmin is available at: http://localhost:8080
echo - Email: admin@policy-etl.com
echo - Password: admin123
echo.
echo Development environment is ready!
echo You can now start your Spring Boot application.
pause
