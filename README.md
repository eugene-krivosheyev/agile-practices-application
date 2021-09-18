# Application architecture
![structure](https://www.planttext.com/api/plantuml/svg/VLBBQiCm4BpxAvRad7n1y27ngJIGDgKNauCihnqHMHAocc9A_hsI7vBiq8AHnUpixkneAqqQX4fBOL2KoNI1JGf6DfIO92c98GLf4R8zhB-lp620ZcyJH6MZf41X87_5cj3k7LjpHdrd52yJoTPtxGf2Rwx9Jm8m84El7W28IfsLPg8-VXvy2KVeZo6nxGbZuYpLzV_0Hd5YS9OzOxqeSjBopahK_v3AWlvHQlmDObP4ASxi84mAg92OBuqhhssXJicM6Qcx2-nsL9POey0oJM7DMNqrjIQTpyiFzbMqvDo9j2-JJxvwnWZqmQYqZmSCDfOJJUqf4JPmeg4zwRcXPSPEHnq3w6GzBSN8V2JXN1oHNLbXBH18dRCdAVdi5MAblQ0bksSjGISgsuTkuoPjjoa4kvUHM7i41tkNaRdJRapxZTjk1VZzjgY_kq0auP7y0W00)
<details>
<summary>puml</summary>

```puml
@startuml
frame frontend
frontend -> tomcat

database DB #white
database MQ #white
component [LegacyRestService] #white

frame backend {
  frame tomcat {
    component [SpringMVC] #white
    
    frame spring {
      component [RestTemplate] #white
      component [JpaProvider] #white
      component [JdbcTemplate] #white
      
      frame "application feature" {
        component [Repository] <<codegened>> #lightgray
        [Controller] -> [Service]
        Service -> [Repository]
        Repository --> JpaProvider
        JpaProvider --> JdbcTemplate
        
        Service --> RestTemplate
        RestTemplate -> LegacyRestService
      }
    }
    
    component [DbConnectionPool] #white
    JdbcTemplate --> DbConnectionPool
    DbConnectionPool -> DB
    
    spring ..> Controller
    spring ..> Service
    spring ..> Repository
    spring ..> JpaProvider
  }
  
  tomcat -> SpringMVC
  SpringMVC -> Controller
}


@enduml
```
</details>

# Setup Maven configuration [in case of corporate Maven repo]
```shell
vi maven-settings.xml
cp maven-settings.xml $M2_HOME/conf/settings.xml
```

# Build with Maven or Gradle
```shell
mvn clean verify [-DexcludedGroups="nope" -Dgroups=""]
gradle clean check bootJar [jacocoTestReport pitest -i --scan --no-build-cache -DexcludedGroups='nope' -Dgroups=""]
```

# Run app with embedded DB
```shell
java -Dderby.stream.error.file=log/derby.log -jar target/dbo-1.0-SNAPSHOT.jar --spring.profiles.active=qa
```
open [http://localhost:8080/dbo/swagger-ui/](http://localhost:8080/dbo/swagger-ui/)

# Run app with stand-alone server DB
```shell
target/db-derby-10.13.1.1-bin/bin/startNetworkServer &
java -jar target/dbo-1.0-SNAPSHOT.jar --spring.profiles.active=qa2
```

# Run legacy system *stub* while QA [optional]
```shell script
cd target/test-classes # cat mappings/legacyAccountingSystemResponse.json
java -jar wiremock-jre8-standalone-2.26.3.jar --verbose --port 8888 # curl localhost:8888/api/account
``` 

# Build and run Docker container for Application [optional]
```bash
docker build -t acme/dbo:1.0-SNAPHOT-it .

docker run -d -p 8080:8080 --name dbo acme/dbo:1.0-SNAPHOT-it
docker exec -it dbo /bin/sh
```
```bash
docker rm dbo -f
```

# Application graceful shutdown
```
curl --request POST http://localhost:8080/dbo/actuator/shutdown
```
