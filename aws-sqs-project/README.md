# aws-sqs-project

## Pre Requisites
* Amazon cli (Only for local)
* Docker Desktop
### Run tests
`$ ./mvnw clean verify`

### Run locally
Start Infra and then issue below command

`$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local`


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

> aws configure
> aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name spring-boot-amazon-sqs

### List SQSQueues
> aws --endpoint-url=http://localhost:4566 sqs list-queues

## command to remove docker
> docker system prune -a -f --volumes
