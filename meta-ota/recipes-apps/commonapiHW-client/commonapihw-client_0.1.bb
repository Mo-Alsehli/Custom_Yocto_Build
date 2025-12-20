DESCRIPTION = "Common API Hello World Client Application"
LICENSE = "CLOSED"

SRC_URI = "file://CMakeLists.txt \
           file://commonapi.ini \
           file://vsomeip.json \
           file://HelloWorldClient.cpp \
           file://src-gen/ \
"

S = "${WORKDIR}"

DEPENDS += " vsomeip commonapi-core-runtime commonapi-someip-runtime boost"

inherit cmake

EXTRA_OECMAKE += " \
    -DCMAKE_PREFIX_PATH=${STAGING_DIR_TARGET}/usr \
    -DCMAKE_BUILD_TYPE=Release \
"

do_install() {
    install -d ${D}/usr/bin
    install -m 0755 ${B}/HelloWorldClient ${D}/usr/bin/

    install -d ${D}${sysconfdir}/commonapi-helloworld/
    install -m 0644 ${WORKDIR}/vsomeip.json ${D}${sysconfdir}/commonapi-helloworld/
    install -m 0644 ${WORKDIR}/commonapi.ini ${D}${sysconfdir}/commonapi-helloworld/
}

FILES_${PN} += " \
    ${sysconfdir}/commonapi-helloworld/ \
"
