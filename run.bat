@echo off
setlocal EnableDelayedExpansion
REM ─────────────────────────────────────────────────────────────
REM  Portfolio Tracker - Build, Run & Test (Windows)
REM  Usage: 
REM    run.bat         (Build & Run interactive CLI)
REM    run.bat test    (Compile & Run all JUnit 5 unit tests)
REM    run.bat clean   (Clean compiled binaries)
REM ─────────────────────────────────────────────────────────────

set MAIN_CLASS=com.portfolio.Main
set SRC_DIR=src\main\java
set TEST_SRC_DIR=src\test\java
set OUT_DIR=out
set OUT_TEST_DIR=out_test
set LIB_DIR=lib
set SQLITE_JAR=%LIB_DIR%\sqlite-jdbc.jar
set SLF4J_API=%LIB_DIR%\slf4j-api.jar
set SLF4J_SIMPLE=%LIB_DIR%\slf4j-simple.jar
set JUNIT_JAR=%LIB_DIR%\junit-platform-console-standalone.jar
set CP=%OUT_DIR%;%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%

echo ======================================
echo   Portfolio Tracker - Build Script
echo ======================================

REM ── FIND JAVA ────────────────────────────────────────────────
set JAVA_CMD=java
set JAVAC_CMD=javac

java -version >nul 2>&1
if %errorlevel% == 0 goto :java_found

if exist "C:\Program Files\Java\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Java\bin\java.exe"
    set "JAVAC_CMD=C:\Program Files\Java\bin\javac.exe"
    goto :java_found
)
if exist "C:\Program Files\Eclipse Adoptium\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Eclipse Adoptium\bin\java.exe"
    set "JAVAC_CMD=C:\Program Files\Eclipse Adoptium\bin\javac.exe"
    goto :java_found
)
if exist "C:\Program Files\Microsoft\jdk-17\bin\java.exe" (
    set "JAVA_CMD=C:\Program Files\Microsoft\jdk-17\bin\java.exe"
    set "JAVAC_CMD=C:\Program Files\Microsoft\jdk-17\bin\javac.exe"
    goto :java_found
)

echo ERROR: Java not found. Please install JDK 17+ from https://adoptium.net
pause
exit /b 1

:java_found
echo Java found.

REM ── DOWNLOAD MISSING JARS ────────────────────────────────────
if not exist "%LIB_DIR%" mkdir "%LIB_DIR%"

if not exist "%SQLITE_JAR%" (
    echo Downloading sqlite-jdbc.jar...
    curl -L -o "%SQLITE_JAR%" "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar"
    if errorlevel 1 ( echo FAILED to download sqlite-jdbc.jar. Place it manually in lib\. & pause & exit /b 1 )
)
if not exist "%SLF4J_API%" (
    echo Downloading slf4j-api.jar...
    curl -L -o "%SLF4J_API%" "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar"
)
if not exist "%SLF4J_SIMPLE%" (
    echo Downloading slf4j-simple.jar...
    curl -L -o "%SLF4J_SIMPLE%" "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar"
)
if not exist "%JUNIT_JAR%" (
    echo Downloading junit-platform-console-standalone.jar...
    curl -L -o "%JUNIT_JAR%" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar"
)

REM ── CLEAN ────────────────────────────────────────────────────
if "%1"=="clean" (
    rmdir /s /q %OUT_DIR% 2>nul
    rmdir /s /q %OUT_TEST_DIR% 2>nul
    echo Cleaned out/ and out_test/.
    goto end
)

REM ── TEST MODE ────────────────────────────────────────────────
if "%1"=="test" (
    echo.
    echo Compiling main sources for tests...
    mkdir %OUT_DIR% 2>nul
    del sources_main.txt 2>nul
    for /r %SRC_DIR% %%f in (*.java) do (
        set "rel=%%f"
        set "rel=!rel:%CD%\=!"
        set "rel=!rel:\=/!"
        echo !rel!>> sources_main.txt
    )
    "%JAVAC_CMD%" -cp "%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%" -d %OUT_DIR% @sources_main.txt
    del sources_main.txt
    if errorlevel 1 ( echo Main compilation failed. & pause & exit /b 1 )

    echo Compiling unit test sources...
    mkdir %OUT_TEST_DIR% 2>nul
    del sources_test.txt 2>nul
    for /r %TEST_SRC_DIR% %%f in (*.java) do (
        set "rel=%%f"
        set "rel=!rel:%CD%\=!"
        set "rel=!rel:\=/!"
        echo !rel!>> sources_test.txt
    )
    "%JAVAC_CMD%" -cp "%OUT_DIR%;%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%;%JUNIT_JAR%" -d %OUT_TEST_DIR% @sources_test.txt
    del sources_test.txt
    if errorlevel 1 ( echo Test compilation failed. & pause & exit /b 1 )

    echo.
    echo Running JUnit 5 Unit Tests...
    echo --------------------------------------
    "%JAVA_CMD%" -jar "%JUNIT_JAR%" execute --class-path "%OUT_DIR%;%OUT_TEST_DIR%;%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%" --scan-class-path
    goto end
)

REM ── BUILD ────────────────────────────────────────────────────
echo.
echo Building project...
mkdir %OUT_DIR% 2>nul
del sources.txt 2>nul
for /r %SRC_DIR% %%f in (*.java) do (
    set "rel=%%f"
    set "rel=!rel:%CD%\=!"
    set "rel=!rel:\=/!"
    echo !rel!>> sources.txt
)
"%JAVAC_CMD%" -cp "%SQLITE_JAR%;%SLF4J_API%;%SLF4J_SIMPLE%" -d %OUT_DIR% @sources.txt
del sources.txt

if errorlevel 1 (
    echo BUILD FAILED. See errors above.
    pause
    exit /b 1
)
echo Build successful!

REM ── RUN ──────────────────────────────────────────────────────
echo.
echo Starting Portfolio Tracker...
echo.
mkdir reports 2>nul
"%JAVA_CMD%" -cp "%CP%" %MAIN_CLASS%

:end
pause
