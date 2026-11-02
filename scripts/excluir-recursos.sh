#!/bin/bash
set -e

GRUPO="rg-clyvo-elo"

az group delete \
  --name "$GRUPO" \
  --yes \
  --no-wait
