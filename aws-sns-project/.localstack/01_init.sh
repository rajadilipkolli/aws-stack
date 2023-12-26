#!/usr/bin/env bash

# Create SNS topic
awslocal sns create-topic --name testTopic --attributes DisplayName="LocalstackTopic"

# List SNS topics
echo "List of SNS Topics:"
echo "-------------------------------"
awslocal sns list-topics
