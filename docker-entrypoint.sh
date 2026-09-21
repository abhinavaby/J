#!/bin/bash
set -e

echo "=== Starting ConnectAI Desktop Container ==="

# 1. Start Xvfb (Virtual Framebuffer Display)
Xvfb :99 -screen 0 ${RESOLUTION} &
sleep 1

# 2. Start Lightweight Window Manager
openbox &
sleep 1

# 3. Start VNC Server
x11vnc -display :99 -forever -shared -nopw -bg -rfbport 5900

# 4. Start noVNC Web Server (WebSockets -> VNC bridge)
HTTP_PORT="${PORT:-6080}"
websockify --web=/usr/share/novnc/ ${HTTP_PORT} localhost:5900 &

echo "========================================================="
echo " ConnectAI is running!"
echo " Web UI (noVNC):  http://localhost:6080/vnc.html"
echo " VNC Direct Port: localhost:5900"
echo "========================================================="

# 5. Launch Java Swing Application
exec java -Dsun.java2d.opengl=false -jar /app/connectai.jar
