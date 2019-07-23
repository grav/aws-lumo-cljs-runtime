# AWS SDK Example
Interact with AWS Services (S3 in this case) using the AWS NodeJS SDK, ClojureScript and
the Lumo-cljs runtime.

## Prerequisites
- a deployed Lumo-cljs runtime (we'll use `arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:13`)
- suitable credentials for AWS
- the aws-cli version 1.16 or newer
- NodeJS version 10 or newer and NPM

## Create execution role (TODO)
- must be able to execute Lambdas
- must have read-access to S3

## Create a Lambda
```
$ npm i # install aws sdk

$ zip -r function.zip aws_sdk_example node_modules

$ aws lambda create-function --function-name lumo-s3 --zip-file fileb://function.zip --handler aws-sdk-example.core/list-buckets --runtime provided --role arn:aws:iam::313836948343:role/lambda-role



```

## Update the Lambda code (if needed)

```
$ aws lambda update-function-code --region eu-west-1  --function-name lumo-s3 --zip-file fileb://function.zip
```

## Set the runtime for the Lambda

```
$ aws lambda update-function-configuration --function-name lumo-s3 --layers arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:13 --region eu-west-1
```

## Invoke the Lambda

```
$ aws lambda invoke --function-name lumo-s3 --region eu-west-1 --payload '{}' response.txt 

$ cat response.txt
```

You should get back a json object with all (accesible) S3 buckets.