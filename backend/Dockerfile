FROM java:8
VOLUME /tmp
ADD /target/admin-server-1.0-RELEASE.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=light","-jar","/app.jar"]
