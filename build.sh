#!/bin/bash
mvn clean install -DskipTests
docker build -t spring-auth-service .
