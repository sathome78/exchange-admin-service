#!/usr/bin/env bash
docker-compose down

mvn clean package -DskipTests

docker rm admin-server

docker rmi admin-service_admin-server

docker-compose up
