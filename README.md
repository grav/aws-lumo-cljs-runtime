This repo contains an implementation of a [custom AWS Lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html)
that enables executions of [ClojureScript](http://clojurescript.org) code in AWS Lambda without any pre-compilation.

It relies on the awesome [Lumo](https://github.com/anmonteiro/lumo) project, and
was inspired by [this episode of The Repl podcast](https://www.therepl.net/episodes/14/).

It's based on the [Tutorial from Amazon](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-walkthrough.html)
as well as the [Node 10.x/11.x implementations from LambCI](https://github.com/lambci/node-custom-lambda).

It's still very alpha, but this document contains a step-by-step guide for getting things started.

Note: there's now a publicly available layer with the runtime available with arn `arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:8`, so you should be able to just skip ahead to [Create function arhive](#create-function-archive) and use that arn.

### Get the pre-built version of Lumo

In order to execute Lumo in an AWS Lambda context, you need a static build with all libraries included. 

You can either get it from here: https://github.com/grav/aws-lumo-cljs-runtime/releases/tag/v1.9 or ...

### ... or clone Lumo fork and build it
The fork of Lumo at https://github.com/grav/lumo is prepared for creating a
static build of Lumo:

```
git clone git@github.com:grav/lumo
```

Build Docker image in this repo:
```
cd /path/to/aws-lumo-cljs-runtime
docker build . -t ami-lumo
```

Build Lumo, pointing out the fork of Lumo:
```
docker run -v /path/to/lumo:/lumo --rm ami-lumo \
```

You'll get an error in the end, but an executable will nevertheless be created in `/path/to/lumo/build/lumo`.

### Create the runtime archive

```
zip -j runtime.zip bootstrap /path/to/lumo runtime.cljs
```

The flag `-j` just ignores paths and puts everything in the archive root.

### Publish layer

A layer can be used by a lambda to pull in additional code. In this context, the layer contains the actual runtime:

```
aws lambda publish-layer-version --layer-name lumo-runtime --zip-file fileb://runtime.zip
```

You'll get an `arn` with a layer version back, which you'll need when configurating the lambda.

### Create function archive
```
zip -r function.zip my_package
```

The `my_package` dir in this repo contains a simple handler, but you can provide your own.

### Create the lambda

For the `--role` parameter, you must supply a role that can execute lambdas.
See https://docs.aws.amazon.com/lambda/latest/dg/runtimes-walkthrough.html#runtimes-walkthrough-prereqs

The `--handler` parameter must correspond to the directory structure of the ClojureScript code that you provide:

```
aws lambda create-function --function-name test-lumo --zip-file fileb://function.zip --handler my-package.my-ns/my-handler --runtime provided --role arn:aws:iam::xxx:role/lambda-role
```

Use the layer `arn` that you received when publishing the layer, including the layer version, to configure the lambda:

```
aws lambda update-function-configuration --function-name test-lumo --layers arn:aws:lambda:eu-west-1:xxx:layer:lumo-runtime:1
```

Note that you can also use the publicly available version of the layer: `arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:8`

### Invoke the lambda
```
aws lambda invoke --function-name test-lumo --payload '{"foo":42}' response.txt
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
      "lambda-runtime-invoked-function-arn": "arn:aws:lambda:eu-west-1:xxx:function:test-lumo"
    }
  }
}
```
