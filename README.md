# MedicineScheduler
## Set up data base
1. set up: https://github.com/mysql/mysql-docker
2. run: docker run --detach --name=mysql1 --env="MYSQL_ROOT_HOST=%" --publish 3306:3306 mysql/mysql-server:5.7

## Branches
1. Master - Dev 
2. Release - Prod

## application.properties
TELEGRAM_API_TOKEN - token from your tg bot  
TELEGRAM_BOT_USERNAME - name from your tg bot  
SCHEDULE_INTERVAL - time interval in seconds for your scheduled class  