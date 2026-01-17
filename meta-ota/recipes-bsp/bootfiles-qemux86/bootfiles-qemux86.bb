SUMMARY = "Custom boot files for qemux86-64 OTA image"
LICENSE = "CLOSED"

SRC_URI = "file://extlinux.conf"

inherit deploy

do_install() {
    install -d ${D}/boot/extlinux
    install -m 0644 ${WORKDIR}/extlinux.conf ${D}/boot/extlinux/extlinux.conf
}

do_deploy() {
    install -d ${DEPLOYDIR}/extlinux
    install -m 0644 ${WORKDIR}/extlinux.conf ${DEPLOYDIR}/extlinux/extlinux.conf
}
addtask deploy after do_install before do_build

FILES:${PN} += "/boot/extlinux/extlinux.conf"
