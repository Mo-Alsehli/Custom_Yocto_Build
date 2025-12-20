SUMMARY = "Static IP configuration for eth0"
DESCRIPTION = "Adds a static IP address to eth0 using systemd-networkd."
PR = "r1"
LICENSE = "CLOSED"

SRC_URI = "file://10-eth0.network"

FILES:${PN} += "/etc/systemd/network/10-eth0.network"

do_install() {
    install -d ${D}/etc/systemd/network
    install -m 0644 ${WORKDIR}/10-eth0.network ${D}/etc/systemd/network/10-eth0.network
}
