#!/usr/bin/env bash
docker run --name db_admin --rm -p 4406:3306 -v /Users/olegpodolian/admindb-test:/var/lib/mysql --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_DATABASE=admin_db" -d mysql:5.7 --character-set-server=utf8

