SUMMARY = "OTA Qt GUI launcher service"
LICENSE = "CLOSED"

SRC_URI = "\
    file://qemux86-env-setup.sh \
"

do_install() {
    # Script
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/qemux86-env-setup.sh ${D}${bindir}/qemux86-env-setup.sh
}

FILES:${PN} = "\
    ${bindir}/qemux86-env-setup.sh \
"
