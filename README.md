# MedicineScheduler
## Set up
## Data base
0. https://github.com/mysql/mysql-docker
1. docker pull mysql/mysql-server:5.7
2. docker run --detach --name=mysql1 --env="MYSQL_ROOT_HOST=%" --publish 3306:3306 mysql/mysql-server:5.7
3. docker logs mysql1 2>&1 | grep GENERATED
4. docker exec -it mysql1 mysql -uroot -p (use password from 4)
5. ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
6. ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
7. FLUSH PRIVILEGES;
8. CREATE DATABASE test_db;
9. docker exec -i mysql1 mysql -uroot -proot test_db < schema_script.sql

## App
1. docker build -t azch/ms:latest -f ./docker/dockerfile .  
2. docker run --name msTest --detach --network="host" azch/ms:latest   

## Branches
1. Master - Dev 
2. Release - Prod

## application.properties
TELEGRAM_API_TOKEN - token from your tg bot  
TELEGRAM_BOT_USERNAME - name from your tg bot  
SCHEDULE_INTERVAL - time interval in seconds for your scheduled class  


 

