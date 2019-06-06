#!/usr/bin/env bash
docker-compose down

docker rm admin-client

docker rmi admin-service_admin-client

docker-compose up
