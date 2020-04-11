## Custom runtime for running ClojureScript on AWS Lambda 

### What is it?

This custom runtime makes it easy to run ClojureScript on AWS Lambda, without having to compile to Javascript.

It uses [Lumo](https://github.com/anmonteiro/lumo) to execute ClojureScript and supports both ClojureScript (jars) and NodeJS dependencies (`node_modules`).

### Purpose

The runtime was initially created to lower the barrier for writing and executing ClojureScript in AWS Lambda: you can write code directly in the AWS Lambda console and execute it by pressing a button. See this short video tutorial: https://vimeo.com/391237884

However, as it supports third party dependencies, both from NodeJS and ClojureScript, it also serves as an easy way of getting meaningful ClojureScript applications running in AWS Lambda.

### How do I get started?

The  [`minimal`](example/minimal) example illustrates how to get going in the simplest way. You can either check out the code in this repo, or  directly type the example into the editor of the AWS Lambda Console.

Eventually you'll want to create more elaborate programs. Checkout the [README in the `example` folder](example) for an overview.

### Layer ARN

The newest version of the runtime is deployed in the `eu-west-1` region, and has the following ARN:

```
arn:aws:lambda:eu-west-1:313836948343:layer:lumo-runtime:20
```

See the [`minimal`](example/minimal) example for info on how to use a custom runtime. 

### Building the runtime

You can build and deploy the runtime yourself. See [`BUILD.md`](BUILD.md) for instructions.

### Background
 
This repo contains an implementation of a [custom AWS Lambda runtime](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-custom.html)
that enables executions of [ClojureScript](http://clojurescript.org) code in AWS Lambda without any pre-compilation.

It relies on the awesome [Lumo](https://github.com/anmonteiro/lumo) project, and
was inspired by [this episode of The Repl podcast](https://www.therepl.net/episodes/14/).

It's based on the [Tutorial from Amazon](https://docs.aws.amazon.com/lambda/latest/dg/runtimes-walkthrough.html)
as well as the [Node 10.x/11.x implementations from LambCI](https://github.com/lambci/node-custom-lambda).

With the help of [Andrea Richiardi](https://github.com/arichiardi), it was grown from a mere proof of concept into a usable project.
