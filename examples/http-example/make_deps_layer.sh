#!/usr/bin/env bash

set -euo pipefail

mkdir -p lib

deps=$(clj -Spath)
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