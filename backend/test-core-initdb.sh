#!/usr/bin/env bash
docker run --name birzha_empty_schema --rm -p 3306:3306 --env="MYSQL_ROOT_PASSWORD=root" --env="MYSQL_DATABASE=birzha_empty_schema" -d mysql:5.7 --character-set-server=utf8 --lower_case_table_names=1
