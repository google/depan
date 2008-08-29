#! /bin/bash
xargs -L 1 svn diff < targets.svn > targets.diff
