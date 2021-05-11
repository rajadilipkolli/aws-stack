# myfirstawsproject

### Run tests
`$ ./mvnw clean verify`

### Run locally
`$ ./mvnw docker:start spring-boot:run`


### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui/index.html
* Actuator Endpoint: http://localhost:8080/actuator

### How to create sqs queue

set region by issuing command by providing below value 

```
 AWS Access Key ID [None]: test
 AWS Secret Access Key [None]: test
 Default region name [None]: us-east-1
 Default output format [None]: json
 
```

* aws configure
* aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name spring-boot-amazon-sqs

### List SQSQueues
* aws --endpoint-url=http://localhost:4566 sqs list-queues