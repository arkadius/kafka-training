#!/usr/bin/env bash

set -e

# This script invoke command inside schemaregistry container passing --bootstrap-server kafka:9092 argument

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <example name> <schemaregistry command> <kafka-* command arguments>"
  echo ""
  echo "example name - single-node, cluster, all-in-one"
  echo "kafka command - kafka-avro-console-consumer, kafka-avro-console-producer, etc."
  exit 1
fi

EXAMPLE_NAME=$1
COMMAND=$2
shift 2

ROOT_DIR=`dirname "$0" | xargs -I{} readlink -f {}/..`
cd "$ROOT_DIR/kafka-docker-$EXAMPLE_NAME"

docker-compose exec schemaregistry "$COMMAND" --bootstrap-server kafka:9092 "$@"
