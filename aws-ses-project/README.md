# aws-ses-project

Amazon Web Services Simple Email Service (SES) is a cloud-based email platform that allows businesses to send and receive emails using their own custom domains. It offers a range of features including email sending and receiving, high deliverability rates, and the ability to scale to meet the needs of large enterprises. It is commonly used for transactional emails, marketing campaigns, and communication with customers.

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
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)
* Kibana: http://localhost:5601/

#Reference
 * https://github.com/thombergs/code-examples/tree/master/aws/springcloudses
