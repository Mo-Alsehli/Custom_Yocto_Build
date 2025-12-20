# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "git://github.com/COVESA/capicxx-core-runtime.git;protocol=https;branch=master"

# Modify these as desired
PV = "3.2.4+git${SRCPV}"
SRCREV = "0e1d97ef0264622194a42f20be1d6b4489b310b5"

S = "${WORKDIR}/git"

DEPENDS += " boost"

# NOTE: unable to map the following CMake package dependencies: Doxygen
inherit cmake pkgconfig

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE = ""

