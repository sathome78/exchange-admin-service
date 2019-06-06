FROM java:8
VOLUME /tmp
ADD /target/admin-service-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=light","-jar","/app.jar"]

COPY ./wrapper.sh /wrapper.sh

CMD ["/wrapper.sh"]
