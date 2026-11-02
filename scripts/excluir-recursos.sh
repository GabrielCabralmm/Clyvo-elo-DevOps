#!/bin/bash
set -e

GRUPO="rg-clyvo-devops"

az group delete \
  --name "$GRUPO" \
  --yes \
  --no-wait
