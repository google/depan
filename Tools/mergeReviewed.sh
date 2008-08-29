#!/bin/bash
rvwLabel=${1:?Arg 1 should be label for reviewed branch}

urlReview=https://google-depan.googlecode.com/svn/reviews/${rvwLabel}

function getCopyRevision {
  svn log --xml --stop-on-copy ${urlReview} | \
    sed -n -e '/<logentry/,+1p' | tail -1 | sed -e 's/ *revision="//' -e 's/">//'
}

function checkReviewed {
  if svn info ${urlReview} 2> /dev/null | grep -q '^URL: ' ; then
    getCopyRevision
  else
    echo Review branch ${rvwLabel} seems to be missing
    exit 1
  fi
}

function main {
  if fromRev=$(checkReviewed); then
    svn merge -r ${fromRev}:head ${urlReview}
  fi
}

main "$@"
