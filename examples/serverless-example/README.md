# Serverless example

The [Serverless Framework](http://serverless.com) supports provided runtimes for AWS Lambda. 

This example shows how to easily deploy a small ClojureScript webservice with the [Lumo custom runtime](http://github.com/grav/aws-lumo-cljs-runtime).

## Usage

Make sure you have AWS credentials adequate for deploying with Serverless (a root user will certainly do!), and simply issue the following:

```bash
$ npm install serverless # install the framework as a node module
$ npx serverless deploy # package and deploy to AWS
```

Serverless should do its magic and create a Lambda function and an API Gateway, and return an overview of the GET endpoint:

```
...
endpoints:
  GET - https://abc123.execute-api.eu-west-1.amazonaws.com/dev/hello
functions:
  hello: runtime-provided-test-dev-hello
...
```

Now, try doing a cURL against the url. You should get some EDN back:
```
$ curl https://abc123.execute-api.eu-west-1.amazonaws.com/dev/hello

{:hello "Hello from my-handler!", :input {:event {"resource" "/hello", "body" nil, ...
```

That's pretty much it!