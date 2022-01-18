#!/bin/bash

awslocal sqs create-queue --queue-name spring-boot-amazon-sqs
echo "List of SQS Queues:"
echo "-------------------------------"
awslocal sqs list-queues
