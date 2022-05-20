# Terrier CRM API

# Starting the project
## Initial setup
### Database
```
docker run --name sample-mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=sample -e MYSQL_USER=user -e MYSQL_PASSWORD=user-password  -d -p 3306:3306 --restart unless-stopped mysql/mysql-server:latest
```
## Using maven
`mvn clean install` - Generate Mapstruct mapping implementations  
`mvn spring-boot:run` - start the project  
## Using intelliJ
- Enable annotation processing
- in **Build, Execution, Deployment > Build tools > Maven > Runner** tick **Delegate IDE build/run actions to Maven**. This should allow bypassing default annotation processing paths being used incorrectly by intelliJ (see https://youtrack.jetbrains.com/issue/IDEA-200481)
- Start as Spring boot project