#!/bin/bash
echo "########### Creating DLQ ###########"
awslocal sqs create-queue --queue-name spring-boot-dead-letter-queue

echo "########### ARN for DLQ ###########"
DLQ_SQS_ARN=$(awslocal --endpoint-url=http://localstack:4566 sqs get-queue-attributes\
                  --attribute-name QueueArn --queue-url=http://localhost:4566/000000000000/spring-boot-dead-letter-queue\
                  |  sed 's/"QueueArn"/\n"QueueArn"/g' | grep '"QueueArn"' | awk -F '"QueueArn":' '{print $2}' | tr -d '"' | xargs)

echo "########### Creating Source queue ###########"
awslocal sqs create-queue --queue-name spring-boot-amazon-sqs \
        --attributes '{
                           "RedrivePolicy": "{\"deadLetterTargetArn\":\"'"$DLQ_SQS_ARN"'\",\"maxReceiveCount\":\"2\"}",
                           "VisibilityTimeout": "10"
                           }'

echo "########### Listing queues ###########"
echo "-------------------------------"
awslocal sqs list-queues

echo "########### Listing Source SQS Attributes ###########"
awslocal sqs get-queue-attributes\
                  --attribute-name All --queue-url=http://localhost:4566/000000000000/spring-boot-amazon-sqs
