## Minimal example

This is a minimal example for the AWS Lumo ClojureScript runtime. It just output its input arguments.

To get started executing ClojureScript in AWS Lambda, you can either use the AWS Lambda web-based console, 
or use the `aws` command line tool.

### Using the AWS Lambda web-based console

[TODO]

### Using the `aws` command line tool

Make sure you have the aws cli installed (`aws`). You'll need a version that supports Lambda layers
(1.16 and onwards should work). 

You can find an installation guide in the [AWS documentation](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html).

#### 1. Create a zip-file with the source code

Clone the repo and go to the `minimal` folder:

```
cd grav/aws-lumo-cljs-runtime/examples/minimal

zip -r function.zip my_package
```

#### 2. Create the Lambda function on AWS

The `--handler` parameter must correspond to the directory structure of the ClojureScript code that you provide,
so for this example, it's `my-package.my-ns/my-handler`.

For the `--role` parameter, you must supply a role that can execute lambdas.
See https://docs.aws.amazon.com/lambda/latest/dg/runtimes-walkthrough.html#runtimes-walkthrough-prereqs

Execute the following, substituting the `--role` part with a real ARN:

```
aws lambda create-function --function-name minimal1 \
  --zip-file fileb://function.zip --handler my-package.my-ns/my-handler \
  --runtime provided --role arn:aws:iam::xxx:role/lambda-role
  --region eu-west-1
```

Notice that we pick the `eu-west-1` region, since there's already a deployed runtime in this region.

#### 3. Add the runtime layer to the function

The runtime needs to be added to the function as a layer. This is done using the runtime's ARN. 

A runtime is already deployed in the `eu-west-1` region, so you can use its ARN for your function, 
since it's created in the same region:

```
aws lambda update-function-configuration --function-name test-lumo \
--layers arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:20

```

#### 4. Invoke the lambda
You can now test the lambda function using:

```
aws lambda invoke --function-name minimal1 --payload '{"foo":42}' response.txt
```

You should receive something like this in `response.txt`:

```
{
  "hello": "Hello from my-handler!",
  "input": {
    "event": {
      "foo": 42
    },
    "context": {
      "aws-request-id": "b64259ce-03e0-11e9-8db3-1bbff8d08d21",
      "lambda-runtime-invoked-function-arn": "arn:aws:lambda:eu-west-1:xxx:function:minimal1"
    }
  }
}
```

That's it!
