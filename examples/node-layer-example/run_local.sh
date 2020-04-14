#!/usr/bin/env bash
set -euo pipefail

if [ -z "$LUMO_BIN_PATH" ]; then echo "LUMO_BIN_PATH required"; exit 1; fi

echo "Installing node dependencies"
( cd nodejs && npm install )

NODE_PATH=./nodejs/node_modules "$LUMO_BIN_PATH" -m runtime-local
