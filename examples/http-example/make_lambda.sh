#!/usr/bin/env bash

set -euo pipefail

if [ -z "$ROLE" ]; then echo "ROLE required"; exit 1; fi
if [ -z "$RUNTIME" ]; then echo "RUNTIME required"; exit 1; fi
if [ -z "$LAYER_VERSION_ARN" ]; then echo "LAYER_VERSION_ARN required"; exit 1; fi

filename=$(mktemp -u).zip

( cd src && zip -r $filename http )

fname=http-example

aws lambda delete-function --function-name $fname || true
aws lambda create-function --function-name $fname \
--runtime provided \
--role $ROLE --handler http.core/handler \
--timeout 30 \
--zip-file fileb://$filename

aws lambda update-function-configuration --function-name $fname --layers "$RUNTIME" "$LAYER_VERSION_ARN"

