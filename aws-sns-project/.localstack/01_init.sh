#!/usr/bin/env bash

# Set variables
LOCALSTACK_ENDPOINT="http://localstack:4566"
DLQ_NAME="my-dead-letter-queue"
SOURCE_QUEUE_NAME="test_queue"

# Create SNS topic
awslocal sns create-topic --name testTopic --attributes DisplayName="LocalstackTopic"

# List SNS topics
echo "List of SNS Topics:"
echo "-------------------------------"
awslocal sns list-topics

echo "########### Creating DLQ ###########"
awslocal sqs create-queue --queue-name "$DLQ_NAME"

echo "########### ARN for DLQ ###########"
DLQ_SQS_ARN=$(awslocal sqs get-queue-attributes \
    --attribute-name QueueArn \
    --queue-url="$LOCALSTACK_ENDPOINT/000000000000/$DLQ_NAME" \
    | jq -r '.Attributes.QueueArn')

echo "########### Creating Source queue ###########"
awslocal sqs create-queue --queue-name "$SOURCE_QUEUE_NAME" \
    --attributes '{
        "RedrivePolicy": "{\"deadLetterTargetArn\":\"'"$DLQ_SQS_ARN"'\",\"maxReceiveCount\":\"2\"}",
        "VisibilityTimeout": "10"
    }'

echo "########### Listing queues ###########"
echo "-------------------------------"
awslocal sqs list-queues
