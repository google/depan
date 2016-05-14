#! /bin/bash
readonly INSTALL_DIR=../../../Install
unzip target/products/com.pnambic.depan.win64a.release-win32.win32.x86_64.zip -d ${INSTALL_DIR}

# Make thing executable that Tycho goofs up
chmod a+x ${INSTALL_DIR}/DepAn.exe
chmod a+x $(find ${INSTALL_DIR}/plugins -name '*.dll')

