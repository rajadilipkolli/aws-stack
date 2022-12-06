# aws-secretmanager-project

AWS Secret Manager is a service that securely stores, manages, and rotates secrets such as database credentials, API keys, and passwords. It helps organizations improve security by storing secrets in a central location and providing fine-grained access control and automatic rotation of secrets. This allows for easier management and reduces the risk of secrets being exposed or compromised. Secret Manager also helps organizations meet compliance requirements by providing encryption and access control mechanisms to protect secrets from unauthorized access.

### Run tests
`$ ./mvnw clean verify`

### Run locally
```
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```


### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
