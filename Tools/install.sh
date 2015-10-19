#! /bin/bash
set -o nounset
set -o errexit

readonly SELF=$(cd $(dirname $0) && pwd)
readonly INSTALL_DIR=DepAn
readonly DEV_DIR=${SELF}/..
readonly REL_PROD=depan-win64-release
readonly ZIP_PROD="${DEV_DIR}/${REL_PROD}/target/products/com.pnambic.depan.win64.release-win32.win32.x86_64.zip"
readonly DBG_PORT=8000

function removeDepan {
  local installDir="$1"
  test -d ${installDir} \
    && echo removing existing ${installDir} \
    && rm -rf ${installDir}
  return 0
}

function installDepan {
  local installDir="$1"
  echo installing ${REL_PROD} at ${installDir}
  unzip -q "${ZIP_PROD}" -d ${installDir}

  # Make things executable that Tycho goofs up
  chmod a+x ${installDir}/DepAn.exe
  chmod a+x $(find ${installDir}/plugins -name '*.dll')
}

function startDepan {
  local installDir="$1"

  echo starting DepAn
  ${installDir}/DepAn &
}

function debugDepan {
  local installDir="$1"
  local port=$2

  echo starting DepAn
  ${installDir}/DepAn -console -consolelog -vmArgs -Xdebug "-Xrunjdwp:server=y,transport=dt_socket,address=${port}" &
}

function parseOpt {
  local opt=$1
  local flag=$2

  # Just the flag means yes
  if [ "--${opt}" == ${flag} ]; then
    echo 1
    return
  fi

  # Empty value syntax means no
  local val=${flag#--${opt}[:=]}
  if [ -z "${val}" ]; then
    echo 0;
    return;
   fi

  # Translate value to yes or no
  grep -qi -e "${val}" <<YES
on
true
YES
  if [ 0 -eq $? ]; then
    echo 1
  else
    echo 0
  fi
}


function die {
  echo >&2 Error: "$@"
  usage
  exit 1
}

function usage {
  cat >&2 <<HELP
Usage: $0 [ opts ]
  --clean: remove existing tree at installation site [on]
  --install: unpack and prepare distribution file [on]
  --start: run DepAn as an new job [off]
  --debug: run DepAn for remote debugging [off]
  --debug-only: disable clean, install [off]
  --help: this message [off]
HELP
}

function main {

  local clean=1
  local install=1
  local start=0
  local debug=0

  while [ $# -gt 0 ]; do
    case $1 in
      --debug-only)
        clean=0
	install=0
	start=0
	debug=1
	shift;;
      --help)
        usage
	exit 0;;

      # wild-cards after fixed to avoid captures
      --clean*)
        clean=$(parseOpt clean $1)
        shift;;
      --install*)
        install=$(parseOpt install $1)
        shift;;
      --start*)
        start=$(parseOpt start $1)
        shift;;
      --debug*)
        debug=$(parseOpt debug $1)
        shift;;
      *)
        die 'Unrecognized parameter' $1 ;;
    esac
  done

  test ${clean} -ne 0 && removeDepan ${INSTALL_DIR}
  test ${install} -ne 0 && installDepan ${INSTALL_DIR}
  if [ ${debug} -ne 0 ]; then
    debugDepan ${INSTALL_DIR} ${DBG_PORT}
  elif [ ${start} -ne 0 ]; then
    startDepan ${INSTALL_DIR}
  fi
}

main "$@"

