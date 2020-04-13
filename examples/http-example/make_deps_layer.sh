#!/usr/bin/env bash

##########################################################
# This script creates a lambda layer with dependencies   #
# from a deps.edn by invoking clj to compute a classpath #
##########################################################

set -euo pipefail

mkdir -p lib

deps=$(clj -Spath)

# extract important parts of classpath (ignore src and org/clojure/*)
jars=$(echo ${deps//:/ } \
  | tr " " "\n" \
  | grep -v src \
  | grep -v 'org\/clojure' \
  | tr "\n" " ")

cp $jars lib

filename=$(mktemp -u).zip

zip -r $filename lib

aws lambda publish-layer-version \
  --layer-name http-example-deps \
  --zip-file fileb://$filename
