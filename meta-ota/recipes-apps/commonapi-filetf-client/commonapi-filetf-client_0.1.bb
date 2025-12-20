DESCRIPTION = "CommonAPI File Transfer Client Example Application"
SUMMARY = "An example application demonstrating CommonAPI File Transfer Client functionality using vsomeip."
LICENSE = "CLOSED"


SRC_URI = "file://CMakeLists.txt \
           file://FileTransferClient.cpp \
           file://src-gen/ \
           file://vsomeip.json \
           file://commonapi.ini \
"

S = "${WORKDIR}"

inherit cmake pkgconfig

DEPENDS += " vsomeip commonapi-core-runtime commonapi-someip-runtime boost"

EXTRA_OECMAKE += " \
    -DCMAKE_PREFIX_PATH=${STAGING_DIR_TARGET}/usr \
    -DCMAKE_BUILD_TYPE=Release \
"


do_install() {
    install -d ${D}/home/root/commonapi-filetf/
    install -d ${D}/home/root/commonapi-filetf/data/client/
    install -m 0755 ${B}/FileTransferClient ${D}/home/root/commonapi-filetf/

    install -m 0644 ${WORKDIR}/vsomeip.json ${D}/home/root/commonapi-filetf/
    install -m 0644 ${WORKDIR}/commonapi.ini ${D}/home/root/commonapi-filetf/
}

FILES:${PN} += " \
    /home/root/commonapi-filetf/FileTransferClient \
    /home/root/commonapi-filetf/vsomeip.json \
    /home/root/commonapi-filetf/commonapi.ini \
    /home/root/commonapi-filetf/data \
    /home/root/commonapi-filetf/data/client \
"