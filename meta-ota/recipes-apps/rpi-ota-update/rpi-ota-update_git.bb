# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# Unable to find any files that looked like license statements. Check the accompanying
# documentation and source headers and set LICENSE and LIC_FILES_CHKSUM accordingly.
#
# NOTE: LICENSE is being set to "CLOSED" to allow you to at least start building - if
# this is not accurate with respect to the licensing of the software being built (it
# will not be in most cases) you must specify the correct value before using this
# recipe for anything other than initial testing/development!
LICENSE = "CLOSED"
LIC_FILES_CHKSUM = ""

SRC_URI = "git://github.com/Mo-Alsehli/Raspberry-Pi-Update-OTA-GUI.git;protocol=https;branch=master \
            file://rpi-ota-update.service \
           "
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

# NOTE: unable to map the following CMake package dependencies: vsomeip3 Qt6 CommonAPI CommonAPI-SomeIP
inherit qt6-cmake

DEPENDS:append = " vsomeip commonapi-core-runtime commonapi-someip-runtime boost qtbase qtquick3d qttools cups qtdeclarative-native qtwayland"
RDEPENDS:${PN} = " qtbase qtquick3d qttools cups qtwayland qtdeclarative"


# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""

do_install:append(){
    install -d ${D}/home/root/rpi-update-ota/
    install -d ${D}/home/root/rpi-update-ota/data/client/

    install -m 0644 ${S}/vsomeip.json ${D}/home/root/rpi-update-ota/
    install -m 0644 ${S}/commonapi.ini ${D}/home/root/rpi-update-ota/

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/rpi-ota-update.service \
        ${D}${systemd_system_unitdir}/rpi-ota-update.service
}

FILES:${PN} += " \
    /home/root/rpi-update-ota/vsomeip.json \
    /home/root/rpi-update-ota/commonapi.ini \
    /home/root/rpi-update-ota/data \
    /home/root/rpi-update-ota/data/client \
    ${systemd_system_unitdir}/rpi-ota-update.service \
"


