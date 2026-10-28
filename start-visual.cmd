@echo off
setlocal
cd /d "%~dp0"
if not defined JAVA_HOME if exist "%USERPROFILE%\.jdks\ms-21.0.13.0.2\bin\java.exe" set "JAVA_HOME=%USERPROFILE%\.jdks\ms-21.0.13.0.2"
set "VISUAL_MAVEN=mvn.cmd"
where mvn.cmd >nul 2>nul
if errorlevel 1 (
    if exist "D:\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
        set "VISUAL_MAVEN=D:\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
    ) else (
        echo Maven was not found. Use the IntelliJ instructions in GRAFIKAI-UTMUTATO.md.
        pause
        exit /b 1
    )
)
call "%VISUAL_MAVEN%" -f visual-pom.xml compile javafx:run
if errorlevel 1 pause
