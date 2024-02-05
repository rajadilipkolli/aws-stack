# aws-dynamodb-project

Amazon DynamoDB is a fully managed NoSQL database service offered by Amazon Web Services (AWS). It provides fast and predictable performance with seamless scalability, allowing users to easily store, retrieve, and manage massive amounts of data. DynamoDB is used for applications that require high performance, low latency, and flexibility in terms of data structure and access patterns. It offers various features such as automatic partitioning, global tables, and Streams, which enable users to build highly scalable and durable applications.

### Run tests
```shell
./mvnw clean verify
```

### Run locally
```shell
$ docker-compose -f docker/docker-compose.yml up -d
$ ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```


### Useful Links
* Swagger UI: http://localhost:8080/swagger-ui.html
* Actuator Endpoint: http://localhost:8080/actuator
* Prometheus: http://localhost:9090/
* Grafana: http://localhost:3000/ (admin/admin)
* Kibana: http://localhost:5601/

### Starting ELK stack along with application
```shell
docker compose -f docker-compose-elk.yml -f docker-compose.yml up
```

### Reference
1. https://codesoapbox.dev/how-to-browse-spring-boot-logs-in-kibana-configuring-the-elastic-stack/
2. 

