#! /bin/bash
# Prepare a SVN target list for submission that spans all of the
# Eclipse projects.
# The result is written to targets.svn
set -o nounset
set -o errexit

statusFile=$(mktemp -t status.XXXXXX)
compareFile=$(mktemp -t compare.XXXXXX)

# Get the full status once
svn status > ${statusFile}

# Strip all 7 lead columns of status info
# and sort, so comm works correctly
sed 's/^.......//' < ${statusFile} | sort > ${compareFile}

# For targets.svn, keep only "interesting" files
# Column 1: Added. Modified, Deleted, or unchanged files
# Column 2; Modified or unchanged properties
# Column 3: Locked or unlocked files
# Column 4: +history or without
# Column 5: only "unswitched" files
# Column 6: locKed or not in this client
# Column 7: only up-to-date files
# Finish in sorted order, so comm works correctly
cat ${statusFile} | \
  grep -v '/.project$' | grep -v '/.classpath$' | \
  grep -v 'targets.svn$' | grep -v 'targets.diff$' | \
  sed 's/^[AMD ][M ][L ][+ ][ ][K ][ ]//' | \
  sort > targets.svn

# Sanity check: Report uninteresting files
echo Client files excluded from targets.svn
comm -23 ${compareFile} targets.svn | xargs svn status --non-recursive
