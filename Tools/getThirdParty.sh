#! /bin/bash -x
mkdir zips

function getThirdPartyZip {
  local rootName=$1
  local verSuffix=$2
  local hostRoot=$3

  local verName=${rootName}${verSuffix}
  local hostPath=${hostRoot}/${verName}

  wget ${hostPath} -o zips/${verName}.log  -O zips/${verName}.zip
  unzip zips/${verName}.zip -d ${rootName}
}

# For DepanApp.prod
# get JOGL. This is plateform specific, and need a user choice.
echo "JOGL API is plateform specific! Please choose your version:
 0: linux amd64
 1: linux i586
 2: macosx ppc
 3: macosx universal
 4: solaris amd64
 5: solaris i586
 6: solaris sparcv9
 7: solaris sparc
 8: windows amd64
 9: windows i586
Enter Your choice nb [default is set to 1]:"
echo -n " >> "
read choice
if [ "$choice" == "0" ]; then jogl_plateform="linux-amd64"; 
elif [ "$choice" == "1" ]; then jogl_plateform="linux-i586"; 
elif [ "$choice" == "2" ]; then jogl_plateform="macosx-ppc"; 
elif [ "$choice" == "3" ]; then jogl_plateform="macosx-universal"; 
elif [ "$choice" == "4" ]; then jogl_plateform="solaris-amd64"; 
elif [ "$choice" == "5" ]; then jogl_plateform="solaris-i586"; 
elif [ "$choice" == "6" ]; then jogl_plateform="solaris-sparcv9"; 
elif [ "$choice" == "7" ]; then jogl_plateform="solaris-sparc"; 
elif [ "$choice" == "8" ]; then jogl_plateform="windows-amd64"; 
elif [ "$choice" == "9" ]; then jogl_plateform="windows-i586"; 
else jogl_plateform="linux-i586"; fi
jogl_version=1.1.0
getThirdPartyZip jogl "-$jogl_version-$jogl_plateform.zip" http://download.java.net/media/jogl/builds/archive/jsr-231-1.1.0
mv jogl/jogl-$jogl_version-$jogl_plateform/* jogl/
rm -rf jogl/jogl-$jogl_version-$jogl_plateform

# For DepanApp.prod & DepanCore.prod
# Get jung 2.0-alpha2
# wget http://downloads.sourceforge.net/jung/jung2-alpha2.zip -o zips/jung2-alpha2.log -O zips/jung2-alpha2.zip
# unzip zips/jung2-alpha2.zip -d jung
getThirdPartyZip jung 2-alpha2 http://downloads.sourceforge.net/jung

# For DepanCore.prod, included in jung
# Get colt
# Get jakarta_commons_collections 4.0

# For DepanCore.prod
# Get asm 3.0
# wget http://download.fr2.forge.objectweb.org/asm/asm-3.0-bin.zip -o zips/asm-3.0-bin.log -O zips/asm-3.0-bin.zip
# unzip zips/asm-3.0-bin.zip -d asm
getThirdPartyZip asm -3.0-bin http://download.fr2.forge.objectweb.org/asm

# For DepanCore.prod
# Get joda-time 1.4
# wget http://downloads.sourceforge.net/joda-time/joda-time-1.4.zip -o zips/joda-time-1.4.log -O zips/joda-time-1.4.zip
# unzip zips/joda-time-1.4.zip -d joda-time
getThirdPartyZip joda-time -1.4 http://downloads.sourceforge.net/joda-time

# For DepanCore.prod
# Get xstream 1.1.3
# mkdir xstream
# wget http://repo1.maven.org/maven2/xstream/xstream/1.1.3/xstream-1.1.3.jar -o zips/xstream-1.1.3.log -O xstream/xstream-1.1.3.jar
echo Automated download returns ERROR 403: Forbidden.
echo Download XStream manually from http://xstream.codehaus.org/download.html

# For DepanCore.test
# Get junit 3.8.2
# wget http://downloads.sourceforge.net/junit/junit3.8.2.zip -o zips/junit3.8.2.log -O zips/junit3.8.2.zip
# unzip zips/junit3.8.2.zip -d junit
getThirdPartyZip junit 3.8.2 http://downloads.sourceforge.net/junit

