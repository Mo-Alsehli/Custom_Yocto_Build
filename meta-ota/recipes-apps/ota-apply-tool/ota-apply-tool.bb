SUMMARY = "OTA apply tool for A/B rootfs extlinux switch"
DESCRIPTION = "Privileged OTA installer that writes inactive rootfs slot and updates extlinux.conf"
LICENSE = "CLOSED"

SRC_URI = "file://ota-apply.cpp \
           file://CMakeLists.txt \
           file://update.version \
          "

S = "${WORKDIR}"

inherit cmake


do_install(){
    install -d ${D}${sbindir}
    install -m 0777 ${B}/ota-apply ${D}${sbindir}

    install -d ${D}/data/updates/
    install -m 0644 ${S}/update.version ${D}/data/updates/
}


FILES:${PN} += " \
    /data/updates/update.version \
    /data/updates/ \
    /data/ \
"