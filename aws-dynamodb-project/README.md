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
* ElasticSearch : http://elastic:changeme@localhost:9200

### Starting ELK stack along localstack using docker for application
```shell
docker compose -f docker-compose-elk.yml -f docker-compose.yml up
```
Navigate to http://localhost:5601 to access Kibana, log in using your Elasticsearch credentials

**Create Index Pattern**
Now we know we have connected logstash with application. Since our ELK stack is up and running, we should be able to see these logs from Kibana dashboard!

In ES, we record data into indices(refer logstash.conf). So, we need indices defined to show data. Let’s create an index pattern for our index — logstash-******.

Go to Management > Stack Management > Kibana > Index Patterns page from left side bar in Kibana UI. When we enter the index name, it will automatically suggest the index name. The reason for that is we have already configured Logstash with a custom conf to create output an index called logstash-****** from input log file

**_Discover application logs_**
Go to “Discover” page from menu. In the left side, you will get a drop down to select the pattern. Select logstash-****** pattern from there.

![Alt text](image.png)

There you go! You have the application logs! 😍❤️

### Reference
1. https://salithachathuranga94.medium.com/integrate-elk-stack-into-spring-boot-application-ae38a6371f86
