#!/bin/bash -e

for i in $(seq 20); do
  curl -o /dev/null -fs -X OPTIONS ${CONJUR_APPLIANCE_URL} > /dev/null && echo "server is up" && break
  echo .
  sleep 2
done