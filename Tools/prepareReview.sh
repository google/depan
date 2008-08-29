#!/bin/bash
rvwLabel=${1:?Arg 1 should be label for review branch}

urlTrunk=https://google-depan.googlecode.com/svn/trunk
urlReview=https://google-depan.googlecode.com/svn/reviews/${rvwLabel}

function ensureReview {
  if svn info ${urlReview} | grep -q '^URL: ' ; then
    echo Recycling existing review branch ${rvwLabel}
    svn delete ${urlReview} -m "Recycling ${rvwLabel} for another change list."
  fi
  svn copy ${urlTrunk} ${urlReview}
}

function main {
  ensureReview
  svn switch ${urlReview}
}

main "$@"
