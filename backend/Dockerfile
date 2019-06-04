FROM java:8
VOLUME /tmp
ADD /app/gradle/src/build/libs/server-user-management-0.0.1.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=light","-jar","/app.jar"]
