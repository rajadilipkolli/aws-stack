# aws-sqs-project

Amazon Simple Queue Service (SQS) is a fully managed message queuing service that enables you to decouple and scale microservices, distributed systems, and serverless applications. SQS eliminates the complexity and overhead associated with managing and operating message oriented middleware, and empowers developers to focus on differentiating work.

With SQS, you can send, store, and receive messages between software components at any volume, without losing messages or requiring other services to be available. SQS enables you to move data between distributed components of your applications that perform different tasks, without losing messages or requiring each component to be always available. This makes it easy to build and operate resilient, scalable, and asynchronous architectures that support the integration of diverse applications and services.

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
