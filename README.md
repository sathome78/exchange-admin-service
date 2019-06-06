This application embodies ...



To run application locally, please, type docker-compose up
this will create only admin-db instance and map it to the current project directory,
this will build angular app (http://localhost:80) and spring boot application (http://localhost:7777) with light profile

code example with access token:
curl -X GET \
  http://localhost:7777/api/users \
  -H 'Authorization: Bearer 7fdbdf78-cbc8-4798-8fee-42fc25092f54' \
