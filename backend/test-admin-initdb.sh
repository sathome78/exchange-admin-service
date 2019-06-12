#!/usr/bin/env bash
docker run --name db_admin_test --rm -p 3406:3306 --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_DATABASE=admin_db_test" -d mysql:5.7 --character-set-server=utf8

