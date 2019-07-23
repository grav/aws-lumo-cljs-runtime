#!/bin/bash

set -euxo pipefail

runtime_name=${RUNTIME_NAME:-lumo-runtime-test}
zipfile=$(mktemp -u).zip

zip -j $zipfile bootstrap ${LUMO_BIN_PATH} runtime.cljs
aws lambda publish-layer-version \
      --layer-name $runtime_name \
      --zip-file fileb://$zipfile | jq -r '.LayerVersionArn'
