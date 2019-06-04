RUNTIME_PACKAGE = runtime.zip

all: ${RUNTIME_PACKAGE}

.PHONY: publish clean

clean:
	rm ${RUNTIME_PACKAGE}

${RUNTIME_PACKAGE}:
ifndef LUMO_BIN_PATH
	$(error LUMO_BIN_PATH is undefined)
endif
	zip -j ${RUNTIME_PACKAGE} bootstrap ${LUMO_BIN_PATH} runtime.cljs

publish: ${RUNTIME_PACKAGE}
	aws lambda publish-layer-version \
      --layer-name lumo-runtime \
      --zip-file fileb://${RUNTIME_PACKAGE}
