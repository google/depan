#! /bin/bash
set -o nounset
set -o errexit

function getRepoPath {
  local set=$(mktemp -p .)
  mvn -q help:effective-settings -Doutput=${set}
  sed -n -e '/localRepository/s:^.*>\(.*\)</.*$:\1:p' ${set}
  rm ${set}
}

function purgeDepanRepo {
  local repo=$1
  rm -rf "${repo}/com/pnambic/depan"
}

function main {
  purgeDepanRepo "$(getRepoPath)"
}

main "$@"

