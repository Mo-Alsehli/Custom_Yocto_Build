SUMMARY = "Vsomeip client example on raspberry pi 4"
DESCRIPTION = "A simple Vsomeip client example application for Raspberry Pi 4"
LICENSE = "CLOSED"


inherit cmake


SRC_URI = "file://CMakeLists.txt \
           file://client-example.cpp \
           file://client.json \
          "

S = "${WORKDIR}"

DEPENDS = "vsomeip boost"

do_install(){
    # binary
    install -d ${D}/home
    install -m 0755 client-example ${D}/home/client-example

    # vsomeip config
    install -m 0644 ${WORKDIR}/client.json ${D}/home/client.json
}

FILES:${PN} += "/home/client-example /home/client.json"