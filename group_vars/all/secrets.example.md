kubectl create secret generic -n admin-service-devtest db-admin-devtest \
--from-literal=url=jdbc:mysql://mysql-dev.microservice-dev.svc.cluster.local/admin_service \
--from-literal=user=user \
--from-literal=password=password

kubectl create secret generic -n admin-service-devtest db-core-devtest \
--from-literal=url=jdbc:mysql://exdbdev.cedlveyttji9.us-east-2.rds.amazonaws.com:3306/dbdev2?useUnicode=true\&characterEncoding=UTF-8\&useSSL=false \
--from-literal=user=user \
--from-literal=password=password

kubectl create secret generic -n admin-service-devtest api-exchange-devtest \
--from-literal=user=user \
--from-literal=password=password

kubectl create secret generic -n admin-service-devtest api-wallets-devtest \
--from-literal=user=user \
--from-literal=password=password