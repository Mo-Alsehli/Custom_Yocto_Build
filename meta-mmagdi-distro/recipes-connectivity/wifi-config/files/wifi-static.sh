#!/bin/sh
# Configure WiFi with static IP on wlan0

CONF="/etc/wpa_supplicant/wpa_supplicant.conf"
IP="192.168.1.250"
NETMASK="255.255.255.0"
GATEWAY="192.168.1.1"
DNS="192.168.1.1"


echo "[*] Starting wpa_supplicant"
killall wpa_supplicant 2>/dev/null
wpa_supplicant -B -i wlan0 -c $CONF

# Disable WiFi power saving (stability)
iw dev wlan0 set power_save off 2>/dev/null || true

# Assign static IP
ifconfig wlan0 $IP netmask $NETMASK up

# Set default gateway
route add default gw $GATEWAY

# Configure DNS
echo "nameserver $DNS" > /etc/resolv.conf

echo "[+] WiFi static setup complete"
