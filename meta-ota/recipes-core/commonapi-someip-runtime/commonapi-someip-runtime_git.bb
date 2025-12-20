# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRC_URI = "git://github.com/COVESA/capicxx-someip-runtime.git;protocol=https;branch=master"

# Modify these as desired
PV = "3.2.4+git${SRCPV}"
SRCREV = "86dfd69802e673d00aed0062f41eddea4670b571"

S = "${WORKDIR}/git"

# NOTE: unable to map the following CMake package dependencies: vsomeip3 CommonAPI
DEPENDS = "boost vsomeip commonapi-core-runtime"

inherit cmake

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE += "\
    -DCOMMONAPI_ROOT_DIR=${STAGING_DIR_TARGET}${prefix} \
    -DCMAKE_PREFIX_PATH=${STAGING_DIR_TARGET}${prefix}/lib/cmake \
    -DCMAKE_INSTALL_LIBDIR=${baselib} \
    -DBOOST_INCLUDEDIR=${STAGING_INCDIR} \
    -DBOOST_LIBRARYDIR=${STAGING_LIBDIR} \
    -DBoost_NO_SYSTEM_PATHS=ON \
    -DBoost_NO_BOOST_CMAKE=ON \
"

