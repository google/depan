#!/bin/bash
set -o nounset
set -o errexit

readonly urlTrunk=https://google-depan.googlecode.com/svn/trunk
readonly urlReview=https://google-depan.googlecode.com/svn/reviews

function guessClient {
  echo $(basename $(dirname $(dirname $(cd $(dirname ${0}); pwd))))
}

function ensureReview {
  local rvwLabel=$1
  local rvwUrl=$2
  local client=$3

  if svn info ${rvwUrl} 2>> svn.log | grep -q '^URL: ' ; then
    echo Recycling existing review branch ${rvwLabel}
    svn delete ${rvwUrl} -m "Recycling ${rvwLabel} for another change list."
  fi
  svn copy ${urlTrunk} ${rvwUrl} \
      -m "Prepare review branch ${rvwLabel} for review from ${client}."
}

function main {
  local rvwLabel=${1:?Arg 1 should be label for review branch}
  local client=${2:-$(guessClient)}

  local rvwUrl=${urlReview}/${rvwLabel}

  ensureReview ${rvwLabel} ${rvwUrl} ${client}
  svn switch ${rvwUrl}
}

main "$@"
