# aws-kinesis-producer-project

### Run tests
`$ ./mvnw clean verify`

### Run locally
```
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## Notes:

- kinesis steam biner works with aws v1 of dynamodb and kinesis, as a result we should still live with spring-aws-cloud version of 2.4.2 and not 3.0.0-M3

### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)
* Kibana: http://localhost:5601/
* Localstack : http://localhost:4566/health
