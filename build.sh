#!/bin/bash
mvn clean install -DskipTests
docker build -t auth-service .
