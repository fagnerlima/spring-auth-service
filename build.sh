#!/bin/bash
mvn clean install
docker build -t spring-auth-service .
