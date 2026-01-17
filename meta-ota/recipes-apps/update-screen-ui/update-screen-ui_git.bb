# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://build/Desktop_Qt_6_9_2-Debug/.qtc/package-manager/LICENSE.conan;md5=71aee2551f0ef50993e0a5f6ae12ee58"

SRC_URI = "git://github.com/Mo-Alsehli/System-Update-Screen-UI-.git;protocol=https;branch=master \
           file://update-screen.service \
           "

# Modify these as desired
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"


# NOTE: unable to map the following CMake package dependencies: Qt6
inherit qt6-cmake systemd

DEPENDS:append = " qtbase qtquick3d qttools cups qtdeclarative-native qtwayland"
RDEPENDS:${PN} = " qtbase qtquick3d qttools cups qtwayland qtdeclarative"


# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""


do_install:append() {
 # Install systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/update-screen.service \
        ${D}${systemd_system_unitdir}/update-screen.service
}

SYSTEMD_AUTO_ENABLE:${PN} = "enable"
SYSTEMD_SERVICE:${PN} = "update-screen.service"
FILES:${PN} += "${systemd_system_unitdir}/update-screen.service"