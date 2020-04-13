## HTTP Example

### What is it?

This example illustrates how to compute dependencies for a project and create a separate layer with the required jar-files.

It consists of a simple namespace that will query the Wikipedia API and do a simple calculation on the result.

The example depends on the [httpurr](https://github.com/funcool/httpurr) client, which in turn has a dependency 
(a transitive dependency from the perspective of the example code) on [promesa](https://docs.aws.amazon.com/lambda/latest/dg/lambda-intro-execution-role.html).

Since Lumo (our runtime) doesn't generate classpaths or download dependencies, 
we can instead calculate the dependencies via [tools.deps](https://clojure.org/guides/deps_and_cli), by specifying them in a 
`deps.edn` file and invoking the `clj` cli.

### Separate layer

Since our dependencies might not update very often, we can add them to AWS Lambda as a separate layer. This way,  we can iterate faster on the application code, by not having to re-upload the jar-files when the dependencies don't change.

### How to use:

There are two scripts with the source code:
- [make_deps_layer.sh](make_deps_layer.sh): calculates the dependencies and creates a lambda-layer with the jar-files
- [make_lambda.sh](make_lambda.sh): creates a lambda function and associates it with the runtime and the dependency-layer

To get going:
1. run `make_deps_layer.sh` and copy the resulting `LayerVersionArn`
2. run `make_lambda.sh` to create the lambda-function, specifying the layer from the first step 
and a [lambda execution role](https://docs.aws.amazon.com/lambda/latest/dg/lambda-intro-execution-role.html):

```
$ LAYER_VERSION_ARN=arn:aws:lambda:...:http-example-deps:1 ROLE=arn:aws:iam:... ./make_lambda.sh
```

3. invoke the lambda with a query:

```
$ aws lambda invoke --function-name http-example --payload '{"query":"clojure"}' response.txt
```

The `response.txt` will contain the function output - in this case the average word-count of the searched articles:

```
$ cat response.txt
2929.6
```
