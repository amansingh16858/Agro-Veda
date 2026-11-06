@echo off
title AgroVeda AI - Dual Engine System (Java 21 + React 19)
echo ==========================================================
echo    AgroVeda AI - Precision Fertilizer Recommendation
echo    Java 21 Spring Boot Backend + React 19 Modern UI
echo ==========================================================

echo [1/2] Starting Java 21 Spring Boot Backend on port 8080...
start "AgroVeda Java Backend" cmd /k "cd backend && java -jar target\agroveda-backend-1.0.0.jar"

timeout /t 3 /nobreak >nul

echo [2/2] Starting React Frontend on http://localhost:5173...
start "AgroVeda Frontend" cmd /k "npm run dev"

echo ==========================================================
echo System is launching!
echo UI URL:       http://localhost:5173/
echo Java Backend: http://localhost:8080/api/health
echo ==========================================================
