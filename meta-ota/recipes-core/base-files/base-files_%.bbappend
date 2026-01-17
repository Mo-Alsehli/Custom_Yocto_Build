FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:qemux86-64 = " file://fstab-qemu"

do_install:append:qemux86-64() {
    rm -f ${D}${sysconfdir}/fstab
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/fstab-qemu ${D}${sysconfdir}/fstab
}

