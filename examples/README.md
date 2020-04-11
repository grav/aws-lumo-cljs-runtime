This directory contains various examples of using the custom runtime in AWS:

#### [minimal](minimal)

A minimal example of executing ClojureScript on AWS Lambda with detailed usage instructions. 


#### [aws-sdk-example](aws-sdk-example)

Demonstrates how to communicates with S3 from ClojureScript, using the AWS SDK. 

#### [lib-example](lib-example)

Demonstrates using 3rd party dependencies. 

TODO: docs

#### [npm-layer](npm-layer)

Consuming nodejs dependencies as a separate layer, to avoid having to re-deploy all dependencies 
when your source code updates 

TODO: docs


#### [serverless-example](serverless-example)

The [Serverless framework](https://serverless.com) also supports custom runtimes. 
This example shows how to easily deploy a small webservice on AWS Lambda and API Gateway that uses ClojureScript.
