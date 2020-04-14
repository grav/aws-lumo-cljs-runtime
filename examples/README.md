## Example code

This directory contains various examples of using the custom runtime in AWS:

#### [minimal](minimal)

A minimal example of executing ClojureScript on AWS Lambda with detailed usage instructions. 

#### [aws-sdk-example](aws-sdk-example)

Demonstrates how to communicates with S3 from ClojureScript, using the AWS SDK. 

#### [http-example](http-example)

Demonstrates how to create a separate layer for dependencies (in this case jars)

#### [node-deps-example](node-deps-example)

Demonstrates how to consuming NodeJS dependencies

#### [lib-example](lib-example)

Demonstrates using 3rd party dependencies. 

TODO: docs

#### [serverless-example](serverless-example)

The [Serverless framework](https://serverless.com) also supports custom runtimes. 
This example shows how to easily deploy a small webservice on AWS Lambda and API Gateway that uses ClojureScript.
