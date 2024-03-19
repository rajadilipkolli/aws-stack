# aws-kinesis-consumer-project

### Run tests
`$ ./mvnw clean verify`

### Run locally
```
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Notes:

 - Starting 4.0.0 kinesis stream binder works with aws v2 of dynamodb, cloudwatch and kinesis
 - The maximum size of a data blob (the data payload before Base64-encoding) is 1 megabyte (MB).
 - Set DEFAULT_BOUNDED_ELASTIC_ON_VIRTUAL_THREADS=true for using virtual threads


### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)
* Kibana: http://localhost:5601/
* Localstack : http://localhost:4566/health
