#!/bin/bash

while ! exec 6<>/dev/tcp/admin-mysql/3306; do
    echo "Trying to connect to MySQL at 3306..."
    sleep 10
done

java -Djava.security.egd=file:/dev/./urandom \
-Dspring.profiles.active=light \
 -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=5005,suspend=n -jar /app.jar
