#! /bin/bash
set -o nounset
set -o errexit

function buildDepan {
  mvn -f depan-neon-master clean install
}

function main {
  buildDepan
}

main "$@"

