#!/usr/bin/env bash
# Quick launch script for ConnectAI
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

if [ ! -f "target/connectai-1.0.0.jar" ]; then
    echo "JAR not found. Building ConnectAI..."
    ./mvnw package -DskipTests
fi

echo "Launching ConnectAI Desktop Application..."
java -jar target/connectai-1.0.0.jar "$@"
