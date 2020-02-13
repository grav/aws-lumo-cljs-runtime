#!/bin/bash

set -euxo pipefail

runtime=$RUNTIME
role=$ROLE

# aws sdk
fname=lumo-s3
zipfile=$(mktemp -u).zip
response_file=$(mktemp)

( cd examples/aws-sdk-example &&
rm -rf node_modules && npm install &&
zip -qr $zipfile aws_sdk_example node_modules
)

aws lambda delete-function --function-name $fname 2> /dev/null || true
aws lambda create-function --function-name $fname --zip-file fileb://$zipfile \
  --runtime provided --role $role --handler aws-sdk-example.core/list-buckets
aws lambda update-function-configuration --function-name $fname --layers "$runtime"
aws lambda invoke --function-name $fname --payload '{}' $response_file | jq -e '.FunctionError | not' || ( cat $response_file && exit 1 )

# jar-dependency in code
fname=lumo-dep-code
zipfile=$(mktemp -u).zip
response_file=$(mktemp)

( cd examples/lib-example &&
zip -qr $zipfile lib_example lib
)

aws lambda delete-function --function-name $fname 2> /dev/null || true
aws lambda create-function --function-name $fname --zip-file fileb://$zipfile \
  --runtime provided --role $role --handler lib-example.core/handler
aws lambda update-function-configuration --function-name $fname --layers "$runtime"
aws lambda invoke --function-name $fname --payload '{}' $response_file | jq -e '.FunctionError | not' || ( cat $response_file && exit 1 )


# jar-dependency in separate layer

fname=lumo-dep-layer
fn_zipfile=$(mktemp -u).zip
layer_zipfile=$(mktemp -u).zip
response_file=$(mktemp)

( cd examples/lib-example &&
zip -qr $fn_zipfile lib_example 
zip -qr $layer_zipfile lib
)

aws lambda delete-function --function-name $fname 2> /dev/null || true
aws lambda create-function --function-name $fname --zip-file fileb://$fn_zipfile \
  --runtime provided --role $role --handler lib-example.core/handler

liblayer=`aws lambda publish-layer-version \
      --layer-name lumo-dep-lib-layer \
      --zip-file fileb://$layer_zipfile | jq -r '.LayerVersionArn'`

aws lambda update-function-configuration --function-name $fname --layers "$runtime" "$liblayer"
aws lambda invoke --function-name $fname --payload '{}' $response_file | jq -e '.FunctionError | not' || ( cat $response_file && exit 1 )

# npm-dependency in separate layer

fname=lumo-npm-layer
fn_zipfile=$(mktemp -u).zip
layer_zipfile=$(mktemp -u).zip
response_file=$(mktemp)

( cd examples/npm-layer &&
zip -qr $fn_zipfile test_require &&
rm -rf some-location/node_modules &&
npm install --prefix some-location && 
ln -sf some-location nodejs &&
zip -qr "$layer_zipfile" nodejs
)

aws lambda delete-function --function-name $fname 2> /dev/null || true
aws lambda create-function --function-name $fname --zip-file fileb://$fn_zipfile \
  --runtime provided --role $role --handler test-require.core/run-tests

liblayer=`aws lambda publish-layer-version \
      --layer-name lumo-dep-lib-layer \
      --zip-file fileb://$layer_zipfile | jq -r '.LayerVersionArn'`

aws lambda update-function-configuration --function-name $fname --layers "$runtime" "$liblayer"
aws lambda invoke --function-name $fname --payload '{}' $response_file | jq -e '.FunctionError | not' || ( cat $response_file && exit 1 )
