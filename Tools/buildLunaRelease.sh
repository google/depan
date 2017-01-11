#! /bin/bash
set -o nounset
set -o errexit

function buildLibraries {
  mvn -f depan-library-master clean install
}

function buildDepan {
  mvn -f depan-luna-master clean install
}

function main {
  buildLibraries
  buildDepan
}

main "$@"

