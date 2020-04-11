## Minimal example

This is a minimal example for the AWS Lumo ClojureScript runtime. It just output its input arguments.

To get started executing ClojureScript in AWS Lambda, you can either use the AWS Lambda web-based console, 
or use the `aws` command line tool.

### Using the AWS Lambda web-based console

If you want to quickly see the runtime in action, you can do so with the editor of the AWS Lambda console. 

You can check out this video that shows you how: https://vimeo.com/391237884, or follow these steps:

#### 1. Log in to AWS and go to the Lambda service 

Pick the Ireland (`eu-west-1`) region, since there's already a deployed runtime in that region

#### 2. Create a new function 

Pick "Author from scratch", choose a function name, and select "Provide your own bootstrap" under "Runtime". Leave the "Permissions" part to the default.

#### 3. Add the AWS Lumo CLJS runtime

Click "Layers" in the Designer, click "Add a layer", click "Provide a layer version ARN", and paste the ARN of the runtime that's already deployed:

```
arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:20
````

#### 4. Write some code

Click the function name in the Designer, scroll down to the editor, and create a new folder: `my_package`, by right-clicking the root-folder and picking "New folder".

Then right click the `my_package` folder and create a new file, `my_ns.cljs`. 

Double-click this file and paste the contents of the [`my_ns.cljs`](my_ns.cljs) file into it. Notice that the editor syntax-highlights the code, which is a nice detail!

Then change the handler to `my-package.my-ns/my-handler`. This string is what the runtime uses to look up the 
entrypoint, and it uses dashes instead of namespaces. 

Then click the "Save" button in the top of the console.

#### 5. Create some test-data and run the function

Just click the "Test" button in the top of the console. You'll be prompted to create a test-event. The default's fine, so just pick a name for the event, and hit "Create".

You'll need to click "Test" again, and probably scroll to the top of the console, to see the Execution result.If you expand it, you should see both the function output, and below that the log output.

That's it!

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
