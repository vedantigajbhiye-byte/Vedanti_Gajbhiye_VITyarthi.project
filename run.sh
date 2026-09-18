#!/bin/bash
# ─────────────────────────────────────────────────────────────
#  Portfolio Tracker - Build, Run & Test (Linux / macOS)
#  Usage: 
#    ./run.sh         (Build & Run interactive CLI)
#    ./run.sh test    (Compile & Run all JUnit 5 unit tests)
#    ./run.sh clean   (Clean compiled binaries)
# ─────────────────────────────────────────────────────────────

MAIN_CLASS="com.portfolio.Main"
SRC_DIR="src/main/java"
TEST_SRC_DIR="src/test/java"
OUT_DIR="out"
OUT_TEST_DIR="out_test"
LIB_DIR="lib"
SQLITE_JAR="$LIB_DIR/sqlite-jdbc.jar"
SLF4J_API="$LIB_DIR/slf4j-api.jar"
SLF4J_SIMPLE="$LIB_DIR/slf4j-simple.jar"
JUNIT_JAR="$LIB_DIR/junit-platform-console-standalone.jar"
CP="$OUT_DIR:$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE"

GREEN='\033[0;32m'; RED='\033[0;31m'; CYAN='\033[0;36m'; NC='\033[0m'

echo -e "${CYAN}======================================"
echo -e "  Portfolio Tracker - Build Script"
echo -e "======================================${NC}"

# ── CHECK JAVA ───────────────────────────────────────────────
if ! command -v javac &> /dev/null; then
    echo -e "${RED}ERROR: javac not found. Please install JDK 17+${NC}"
    echo "  Ubuntu/Debian: sudo apt install default-jdk"
    echo "  macOS:         brew install openjdk"
    echo "  Download:      https://adoptium.net"
    exit 1
fi
echo -e "${GREEN}Java found: $(java -version 2>&1 | head -1)${NC}"

# ── DOWNLOAD MISSING JARS ────────────────────────────────────
download_jar() {
    local url=$1; local dest=$2; local name=$3
    if [ ! -f "$dest" ]; then
        echo "Downloading $name..."
        curl -fL -o "$dest" "$url" 2>/dev/null || wget -q -O "$dest" "$url" 2>/dev/null
        if [ $? -ne 0 ]; then
            echo -e "${RED}Failed to download $name. Place it manually in lib/${NC}"
            exit 1
        fi
        echo -e "${GREEN}Downloaded $name${NC}"
    fi
}

mkdir -p "$LIB_DIR"
download_jar "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.45.1.0.jar" "$SQLITE_JAR" "sqlite-jdbc.jar"
download_jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar" "$SLF4J_API" "slf4j-api.jar"
download_jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar" "$SLF4J_SIMPLE" "slf4j-simple.jar"
download_jar "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar" "$JUNIT_JAR" "junit-platform-console-standalone.jar"

# ── CLEAN ────────────────────────────────────────────────────
if [ "$1" == "clean" ]; then
    rm -rf "$OUT_DIR" "$OUT_TEST_DIR"
    echo "Cleaned."; exit 0
fi

# ── TEST MODE ────────────────────────────────────────────────
if [ "$1" == "test" ]; then
    echo ""
    echo "Compiling main sources for tests..."
    mkdir -p "$OUT_DIR"
    MAIN_SOURCES=$(find "$SRC_DIR" -name "*.java")
    javac -cp "$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE" -d "$OUT_DIR" $MAIN_SOURCES
    if [ $? -ne 0 ]; then
        echo -e "${RED}Main compilation failed.${NC}"; exit 1
    fi

    echo "Compiling unit test sources..."
    mkdir -p "$OUT_TEST_DIR"
    TEST_SOURCES=$(find "$TEST_SRC_DIR" -name "*.java")
    javac -cp "$OUT_DIR:$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE:$JUNIT_JAR" -d "$OUT_TEST_DIR" $TEST_SOURCES
    if [ $? -ne 0 ]; then
        echo -e "${RED}Test compilation failed.${NC}"; exit 1
    fi

    echo ""
    echo "Running JUnit 5 Unit Tests..."
    echo "--------------------------------------"
    java -jar "$JUNIT_JAR" execute --class-path "$OUT_DIR:$OUT_TEST_DIR:$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE" --scan-class-path
    exit 0
fi

# ── BUILD ────────────────────────────────────────────────────
echo ""
echo "Building project..."
mkdir -p "$OUT_DIR"
SOURCES=$(find "$SRC_DIR" -name "*.java")
javac -cp "$SQLITE_JAR:$SLF4J_API:$SLF4J_SIMPLE" -d "$OUT_DIR" $SOURCES

if [ $? -ne 0 ]; then
    echo -e "${RED}BUILD FAILED. See errors above.${NC}"; exit 1
fi
echo -e "${GREEN}Build successful!${NC}"

# ── RUN ──────────────────────────────────────────────────────
echo ""
echo "Starting Portfolio Tracker..."
echo ""
mkdir -p reports
java -cp "$CP" "$MAIN_CLASS"
