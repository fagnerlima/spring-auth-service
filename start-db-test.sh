#!/bin/bash
docker rm -f auth-db-test
docker run --name auth-db-test -p 5432:5432 \
  -e POSTGRES_DB=auth \
  -e POSTGRES_USER=user.auth \
  -e POSTGRES_PASSWORD=user.pass \
  -d postgres:10-alpine
