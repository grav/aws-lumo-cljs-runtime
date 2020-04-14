## NodeJS Layer example

This example illustrates how to use NodeJS dependencies.

The runtime will look for dependencies in any `node_modules` directory in the lambda-code.

In this example, the `nodejs` directory contains a package.json file that both specifies
an external dependency (is-odd) as well as a local dependency (meaning-of-life).

### Running the example in AWS:

- create the lambda by specifying the runtime and a [lambda execution role](https://docs.aws.amazon.com/lambda/latest/dg/lambda-intro-execution-role.html):
```
$ RUNTIME=arn:... ROLE=arn:... ./make_lambda.sh
```

- invoke the lambda:
```
$ aws lambda invoke --function-name node-deps-example --payload '' response.txt
```

The `response.txt` should contain some json.

### Creating a separate layer for the NodeJS dependencies

Creating a separate layer for dependencies can speed up the development process,
since you do not need to upload dependencies every time your application code changes.

The runtime will look for a `nodejs/node_modules` directory in additional layers, 
so you just need to create a layer with the `nodejs` directory containing the `node_modules` 
folder and associate the lambda-function with this layer.

See the [`http-example`](../http-example) for details on how to do this.

