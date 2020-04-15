# Build

This Lumo AWS Lambda runtime relies on a lumo binary to be built and deployed.

## Compiling Lumo with Musl

In order to avoid library mismatch AWS suggests to statically compile binaries and this is exactly what we are going to do thanks to [Andrea Richiardi](https://github.com/arichiardi)'s [docker-lumo-musl](https://github.com/arichiardi/docker-lumo-musl).

First things first, pull down [the docker image](https://cloud.docker.com/repository/docker/arichiardi/lumo-musl-ami):

```shell
docker pull arichiardi/lumo-musl-ami
```

Second, clone `lumo`:

```shell
git clone git@github.com:anmonteiro/lumo   # anywhere on your filesystem
```

Finally, build using the image:

```
docker run -v /path/to/lumo:/lumo -v /home/user/.m2:/root/.m2 -v /home/user/.boot/cache:/.boot/cache --rm lumo-musl-ami
```

The `/root/.m2` and `/.boot/cache` mappings are optional but recommended for
avoiding downloading dependencies multiple times.

The (long) process will compile the lumo static binary under `/path/to/lumo/build`.

## Building the runtime and publishing it as a Lambda layer

The supplied `Makefile` in this repo will take care of the details:

```
make clean # (if necessary)
LUMO_BIN_PATH=/path/to/lumo make # point to lumo binary from the step above
make publish
```
