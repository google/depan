#! /bin/bash -x
mkdir zips

function downloadFile {
  local src=$1
  local dst=$2
  local log=$3

  # curl is built in on Macs, but wget is a separate tool
  curl --location --url ${src} --output ${dst}
  # wget ${src} -o ${log} -O ${dst}
}

function unpackFile {
  local srcZip=$1
  local dstDir=$2

  mkdir -p $(dirname ${dstDir})
  unzip ${srcZip} -d ${dstDir}
}

function getThirdPartyZip {
  local rootName=$1
  local verSuffix=$2
  local hostRoot=$3
  local unzipDir=${4:-${rootName}}

  local verName=${rootName}${verSuffix}
  local hostPath=${hostRoot}/${verName}

  # some versions of zip require a .zip suffix
  local unzipFile=zips/${verName}.zip

  # Only download destination file if it doesn't exist
  # Delete it if you need to retry the download
  if [[ ! -e "${unzipFile}" ]]; then
    downloadFile ${hostPath} ${unzipFile} zips/${verName}.log
  fi

  # Only unpack into the destination directory if it's not already there
  # Delete destination directory if you need to retry the download
  if [[  ! -e ${unzipDir} ]]; then
    unpackFile ${unzipFile} ${unzipDir}
  fi
}

function getJoglSuffix {
  local jogl_version=1.1.0

  # JOGL is plateform specific, and need a user choice.
  cat >&2 <<PROMPT
JOGL API is platform specific! Please choose your version:
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
[default is linux-i586]
PROMPT
  echo >&2 -n "Enter your choice # >>"

  read choice
  if [ "$choice" == "0" ]; then jogl_platform="linux-amd64"; 
  elif [ "$choice" == "1" ]; then jogl_platform="linux-i586"; 
  elif [ "$choice" == "2" ]; then jogl_platform="macosx-ppc"; 
  elif [ "$choice" == "3" ]; then jogl_platform="macosx-universal"; 
  elif [ "$choice" == "4" ]; then jogl_platform="solaris-amd64"; 
  elif [ "$choice" == "5" ]; then jogl_platform="solaris-i586"; 
  elif [ "$choice" == "6" ]; then jogl_platform="solaris-sparcv9"; 
  elif [ "$choice" == "7" ]; then jogl_platform="solaris-sparc"; 
  elif [ "$choice" == "8" ]; then jogl_platform="windows-amd64"; 
  elif [ "$choice" == "9" ]; then jogl_platform="windows-i586"; 
  else jogl_platform="linux-i586"; fi
  
  echo "-${jogl_version}-${jogl_platform}.zip"
}

# For DepanApp.prod
# get JOGL. This is plateform specific, and need a user choice.
getThirdPartyZip jogl  $(getJoglSuffix 1.1.0) http://download.java.net/media/jogl/builds/archive/jsr-231-1.1.0

# For DepanApp.prod & DepanCore.prod
# Get jung 2.0-beta1
# For DepanCore.prod, included in jung
# Get colt
# Get jakarta_commons_collections 4.0
getThirdPartyZip jung 2-beta1.zip http://downloads.sourceforge.net/jung jung/jung2-beta1

# For DepanCore.prod
# Get asm 3.1
getThirdPartyZip asm -3.1-bin http://download.forge.objectweb.org/asm

# For DepanCore.prod
# Get joda-time 1.6
getThirdPartyZip joda-time -1.6.zip http://downloads.sourceforge.net/joda-time

# For DepanCore.prod
# Get xstream 1.3
getThirdPartyZip xstream -distribution-1.3-bin.zip http://repository.codehaus.org/com/thoughtworks/xstream/xstream-distribution/1.3

# For DepanCore.test
# Get junit 3.8.2
getThirdPartyZip junit 3.8.2.zip http://downloads.sourceforge.net/junit

