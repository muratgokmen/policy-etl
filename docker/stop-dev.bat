@echo off
echo Stopping Policy ETL Development Environment
echo ==========================================

echo Stopping all containers...
docker-compose down

echo Development environment stopped!
pause
