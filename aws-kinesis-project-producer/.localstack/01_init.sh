#!/bin/bash

awslocal kinesis create-stream --stream-name my-test-stream --shard-count 1

awslocal dynamodb create-table \
--table-name spring-stream-lock-registry \
--attribute-definitions AttributeName=lockKey,AttributeType=S AttributeName=sortKey,AttributeType=S \
--key-schema AttributeName=lockKey,KeyType=HASH AttributeName=sortKey,KeyType=RANGE \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack

awslocal dynamodb create-table \
--table-name spring-stream-metadata \
--attribute-definitions AttributeName=KEY,AttributeType=S \
--key-schema AttributeName=KEY,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack

awslocal dynamodb list-tables
awslocal kinesis list-streams