#!/usr/bin/env bash

awslocal kinesis create-stream --stream-name my-test-stream --shard-count 1

awslocal dynamodb create-table \
--table-name spring-stream-lock-registry \
--attribute-definitions AttributeName=lockKey,AttributeType=S \
--key-schema AttributeName=lockKey,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack \
--billing-mode PROVISIONED

awslocal dynamodb create-table \
--table-name spring-stream-metadata \
--attribute-definitions AttributeName=metadataKey,AttributeType=S \
--key-schema AttributeName=metadataKey,KeyType=HASH \
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
--tags Key=Owner,Value=localstack \
--billing-mode PROVISIONED

awslocal dynamodb list-tables
awslocal kinesis list-streams