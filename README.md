This application embodies ...



To run application locally, please, type docker-compose up
this will create only admin-db instance and map it to the current project directory,
this will build angular app (http://localhost:80) and spring boot application (http://localhost:7777) with light profile

code example with access token:
curl -X GET \
  http://localhost:7777/api/users \
  -H 'Authorization: Bearer 7fdbdf78-cbc8-4798-8fee-42fc25092f54' \
  
 
  
***** RUNNING DAO (JAVA) TEST LOCALLY ******

1. run schema migration tool with profile empty-schema 
 IF YOU LIKE DOCKER (docker run --name birzha_empty_schema --rm -p 3306:3306 --env="MYSQL_ROOT_PASSWORD=root" 
                        --env="MYSQL_DATABASE=birzha_empty_schema" -d mysql:5.7 --character-set-server=utf8 --lower_case_table_names=1)
                        
  OR SIMPLY EXECUTE ./test-core-initdb.sh (DO NOT FORGET MAKE IT EXECUTABLE)
  
  P.S. every time schema will be recreated as empty
  
2. up mysql server for local admin testing   
    IF YOU LIKE DOCKER (docker run --name db_admin_test --rm -p 3406:3306 --env="MYSQL_ROOT_PASSWORD=root" 
                        --env="MYSQL_DATABASE=admin_db_test" -d mysql:5.7 --character-set-server=utf8)
                        
  OR SIMPLY EXECUTE ./test-admin-initdb.sh (DO NOT FORGET MAKE IT EXECUTABLE)
  
  P.S. ALL THE MAGIC OF POPULATING DATABASES IS DONE WITH FLYWAY USING REPEATABLE SCRIPTS
    - to add some data to core database /test/resources/db/data/core/.... (please start with R__)
    - to add some data to admin database /test/resources/db/data/admin/....  (please start with R__)
    
 ENJOY
