# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   LICENSE
LICENSE = "Unknown"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI = "git://github.com/COVESA/vsomeip.git;protocol=https;branch=master"

# Modify these as desired
PV = "1.0+git${SRCPV}"
SRCREV = "c4e0db329da9b63f511f3c2456c040582daf9305"

S = "${WORKDIR}/git"

# NOTE: unable to map the following CMake package dependencies: Doxygen benchmark
DEPENDS = "systemd boost"

inherit cmake pkgconfig

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""

do_install:append() {
        install -d ${D}${prefix}/etc/vsomeip
        cp -r ${S}/config/*.json ${D}${prefix}/etc/vsomeip/
}

FILES:${PN} += " \
    ${bindir} \
    ${prefix}/etc/vsomeip \
    ${prefix}/etc/*.json \
"

