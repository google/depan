#!/bin/bash
# Release a client from "in review" state.
# Basically, switch back to trunk, and delete the review branch
# Deleting the review branch makes it easier to keep track on
# pending and completed reviews.
set -o nounset
set -o errexit

# Collect the URL and version info for this client
readonly clientUrl=$(svn info | sed -n -e 's/URL: //p')
readonly revision=$(svn info | sed -n -e 's/Last Changed Rev: //p')
readonly clientName=$(basename ${clientUrl})

readonly REVIEWS=https://google-depan.googlecode.com/svn/reviews
readonly TRUNK=https://google-depan.googlecode.com/svn/trunk

# Don't accidentally delete trunk
if [[ "${clientUrl}" == "${TRUNK}" ]]; then
  echo This client is already linked to the trunk.
  echo "  URL: ${clientUrl}"
  exit 1
fi

# Don't accidentaly delete some other branch
readonly clientDir=$(dirname "${clientUrl}")
if [[ "${clientDir}" != "${REVIEWS}" ]]; then
  echo This client does not appear to be linked to a review branch.
  echo "  URL: ${clientUrl}"
  exit 1
fi

function confirm {
  echo -n Really delete review branch ${clientName} [y]: 1>&2
  line
}

# Ok to do the work.
function main {
  svn switch "${TRUNK}"
  if [[ "$(confirm)" == 'y' ]]; then
    svn delete "${clientUrl}" \
      -m "Releasing committed review branch ${clientName} r${revision}"
  fi
}

main
