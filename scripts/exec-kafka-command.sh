#!/usr/bin/env bash

set -e

# This script invoke command inside kafka container passing --bootstrap-server localhost:9092 argument

if [[ $# -lt 2 ]]; then
  echo "Usage: $0 <example name> <kafka command> <kafka-* command arguments>"
  echo ""
  echo "example name - single-node, cluster, avro, all-in-one"
  echo "kafka command - kafka-console-consumer, kafka-console-producer, kafka-topics, etc."
  exit 1
fi

EXAMPLE_NAME=$1
COMMAND=$2
shift 2

ROOT_DIR=`dirname "$0" | xargs -I{} readlink -f {}/..`
cd "$ROOT_DIR/kafka-docker-$EXAMPLE_NAME"

docker-compose exec kafka "$COMMAND" --bootstrap-server localhost:9092 "$@"
