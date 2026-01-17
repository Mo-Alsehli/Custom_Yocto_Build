#!/bin/sh
set -e

# ---- Network configuration ----
ip link set enp0s4 up
ip addr add 192.168.100.10/24 dev enp0s4 || true
ip route add default via 192.168.100.1 || true

# Multicast route for SOME/IP
ip route add 224.224.224.245 dev enp0s4 || true

# ---- Qt / Wayland configuration ----
export QT_QPA_PLATFORM=wayland
export QT_WAYLAND_DISABLE_WINDOWDECORATION=1
export QT_WAYLAND_SHELL_INTEGRATION=xdg-shell

# Force software rendering
export QT_QUICK_BACKEND=software
export LIBGL_ALWAYS_SOFTWARE=1

# ---- Wayland runtime ----
export XDG_RUNTIME_DIR=/run/user/1000

if [ -e "$XDG_RUNTIME_DIR/wayland-1" ]; then
    export WAYLAND_DISPLAY=wayland-1
else
    export WAYLAND_DISPLAY=wayland-0
fi

export QT_QUICK_BACKEND=software
# ---- Middleware ----
export COMMONAPI_CONFIG=/home/root/rpi-update-ota/commonapi.ini
export VSOMEIP_CONFIGURATION=/home/root/rpi-update-ota/vsomeip.json

