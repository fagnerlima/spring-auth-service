#!/bin/bash
docker rm -f auth-db-test
docker run --name auth-db-test -p 5432:5432 \
  -e POSTGRES_DB=auth-test \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -d postgres:10-alpine
